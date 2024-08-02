package com.cupid.qufit.global.utils.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.NamedValue;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.ESVideoRoomException;
import com.cupid.qufit.global.utils.elasticsearch.dto.RecommendRoomDTO.Request;
import com.cupid.qufit.global.utils.elasticsearch.entity.ESParticipant;
import com.cupid.qufit.global.utils.elasticsearch.entity.ESVideoRoom;
import com.cupid.qufit.global.utils.elasticsearch.repository.ESVideoRoomRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ESVideoRoomService implements ESService<ESVideoRoom> {

    private final ESVideoRoomRepository esVideoRoomRepository;
    private final ElasticsearchClient elasticsearchClient;


    @Override
    public ESVideoRoom save(ESVideoRoom esVideoRoom) {
        return esVideoRoomRepository.save(esVideoRoom);
    }

    @Override
    public ESVideoRoom findById(Long id) {
        return esVideoRoomRepository.findById(id)
                                    .orElseThrow(() -> new ESVideoRoomException(ErrorCode.VIDEO_ROOM_NOT_FOUND));
    }

    @Override
    public Iterable<ESVideoRoom> findAll() {
        return esVideoRoomRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        esVideoRoomRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        esVideoRoomRepository.deleteAll();
    }

    private List<FieldValue> toFieldValueList(List<String> terms) {
        return terms.stream().map(FieldValue::of).toList();
    }

    public List<Long> search(Request request) throws IOException {
        List<FieldValue> mbtis = toFieldValueList(request.getMBTIs());
        List<FieldValue> personalities = toFieldValueList(request.getPersonalities());
        List<FieldValue> hobbies = toFieldValueList(request.getHobbies());

        // bioQuerys 만들기
        List<String> bioQuerys = new ArrayList<>(); // 리스트 형태
        bioQuerys.addAll(request.getHobbies());
        bioQuerys.addAll(request.getPersonalities());
        String bioQueryString = String.join(" ", bioQuerys);    // String 형태

        // BoolQuery
        Query query = BoolQuery
                .of(b -> b.mustNot(m -> m.term(t -> t.field("gender").value(request.getGender())))
                          .filter(f -> f.term(t -> t.field("location").value(request.getLocation())))
                          .filter(f -> f.range(r -> r.field("birth_year")
                                                     .gte(JsonData.of(request.getBirthYear() - request.getTypeAgeMax()))
                                                     .lte(JsonData.of(
                                                             request.getBirthYear() + request.getTypeAgeMin()))))
                          .should(s -> s.terms(
                                  t -> t.field("mbtis").terms(TermsQueryField.of(fv -> fv.value(mbtis))).boost(4.0f)))
                          .should(s -> s.terms(
                                  t -> t.field("personalities").terms(TermsQueryField.of(fv -> fv.value(personalities)))
                                        .boost(3.0f)))
                          .should(s -> s.terms(
                                  t -> t.field("hobbies").terms(TermsQueryField.of(fv -> fv.value(hobbies)))
                                        .boost(2.0f)))
                          .should(s -> s.match(
                                  m -> m.field("bio").query(bioQueryString).boost(1.0f).operator(
                                          Operator.Or)))
                          .minimumShouldMatch("1"))
                ._toQuery();

        // FunctionScoreQuery
        String combinedScript = generateCombinedScript(request.getHobbies(), request.getPersonalities(), bioQuerys);

        Query functionScoreQuery = FunctionScoreQuery.of(f -> f
                .query(query)
                .functions(fs -> fs
                        .scriptScore(ss -> ss
                                .script(Script.of(s -> s
                                        .inline(i -> i.source(combinedScript))))))
                .boostMode(FunctionBoostMode.Sum)
                .scoreMode(FunctionScoreMode.Sum)
        )._toQuery();

        // SearchRequest
        SearchRequest searchRequest = SearchRequest.of(sr -> sr
                .index("participants")
                .query(functionScoreQuery)
                .aggregations("video_rooms", a -> a.terms(t -> t.field("video_room_id").order(List.of(
                                                           NamedValue.of("total_score", SortOrder.Desc))).size(5))
                                                   .aggregations("total_score", a2 -> a2.sum(s -> s.field("_score"))))
        );

        SearchResponse<ESParticipant> response = elasticsearchClient.search(searchRequest, ESParticipant.class);

        List<Long> videoRoomKeys = new ArrayList<>();
        Aggregate videoRoomsAgg = response.aggregations().get("video_rooms");
        if (videoRoomsAgg != null && videoRoomsAgg.isSterms()) {
            List<StringTermsBucket> buckets = videoRoomsAgg.sterms().buckets().array();
            for (StringTermsBucket bucket : buckets) {
                videoRoomKeys.add(Long.valueOf(bucket.key()._get().toString()));
                System.out.println(Long.valueOf(bucket.key()._get().toString()));
            }
        }


        return videoRoomKeys;
    }

    private String generateCombinedScript(List<String> hobbies, List<String> personalities, List<String> bioKeywords) {
        StringBuilder scriptBuilder = new StringBuilder("int score = 0;");

        appendScore(scriptBuilder, hobbies, "hobbies");
        appendScore(scriptBuilder, personalities, "personalities");
        appendScore(scriptBuilder, bioKeywords, "bio");

        scriptBuilder.append("return score;");
        return scriptBuilder.toString();
    }

    private void appendScore(StringBuilder scriptBuilder, List<String> keywords, String field) {
        for (String keyword : keywords) {
            scriptBuilder.append("if (params['_source']['").append(field).append("'] != null");
            if (!field.equals("bio")) {
                scriptBuilder.append(" && params['_source']['").append(field).append("'].contains('").append(keyword)
                             .append("')");
            } else {
                scriptBuilder.append(" && params['_source']['bio'].contains('").append(keyword).append("')");
            }
            scriptBuilder.append(") { score += 1; }");
        }
    }
}
