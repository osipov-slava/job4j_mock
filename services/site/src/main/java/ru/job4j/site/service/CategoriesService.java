package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.InterviewDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CategoriesService {
    private final TopicsService topicsService;

    public List<CategoryDTO> getAll() throws JsonProcessingException {
        var text = new RestAuthCall("http://localhost:9902/categories/").get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public List<CategoryDTO> getPopularFromDesc() throws JsonProcessingException {
        var text = new RestAuthCall("http://localhost:9902/categories/most_pop").get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public CategoryDTO create(String token, CategoryDTO category) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var out = new RestAuthCall("http://localhost:9902/category/").post(
                token,
                mapper.writeValueAsString(category)
        );
        return mapper.readValue(out, CategoryDTO.class);
    }

    public void update(String token, CategoryDTO category) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        new RestAuthCall("http://localhost:9902/category/").put(
                token,
                mapper.writeValueAsString(category)
        );
    }

    public void updateStatistic(String token, int categoryId) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        new RestAuthCall("http://localhost:9902/category/statistic").put(
                token, mapper.writeValueAsString(categoryId));
    }

    public List<CategoryDTO> getAllWithTopics() throws JsonProcessingException {
        var categoriesDTO = getAll();
        for (var categoryDTO : categoriesDTO) {
            categoryDTO.setTopicsSize(topicsService.getByCategory(categoryDTO.getId()).size());
        }
        return categoriesDTO;
    }

    public List<CategoryDTO> getMostPopular() throws JsonProcessingException {
        var categoriesDTO = getPopularFromDesc();
        for (var categoryDTO : categoriesDTO) {
            categoryDTO.setTopicsSize(topicsService.getByCategory(categoryDTO.getId()).size());
        }
        return categoriesDTO;
    }

    public String getNameById(List<CategoryDTO> list, int id) {
        String result = "";
        for (CategoryDTO category : list) {
            if (id == category.getId()) {
                result = category.getName();
                break;
            }
        }
        return result;
    }

    public Map<Integer, Long> getCountNewInterviewsByCategory(List<CategoryDTO> categoryDTOS,
                                                              List<InterviewDTO> newInterviews) throws JsonProcessingException {
        Map<Integer, Long> interviewByCategory = new HashMap<>();
        if (!categoryDTOS.isEmpty()) {
            Map<Integer, Long> interviewByTopic = newInterviews.stream()
                    .collect(Collectors.groupingBy(InterviewDTO::getTopicId, Collectors.counting()));

            for (CategoryDTO categoryDTO : categoryDTOS) {
                var topics = topicsService.getByCategory(categoryDTO.getId());
                topics.forEach(topicDTO -> {
                    var value = interviewByTopic.getOrDefault(topicDTO.getId(), 0L);
                    var sum = interviewByCategory.getOrDefault(topicDTO.getCategory().getId(), 0L) + value;
                    interviewByCategory.put(topicDTO.getCategory().getId(), sum);
                });
            }
        }
        return interviewByCategory;
    }
}
