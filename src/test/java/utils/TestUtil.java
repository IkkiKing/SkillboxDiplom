package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


public class TestUtil {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    public TestUtil(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public String sendPost(String url, ResultMatcher resultMatcher, Object model) throws Exception {
        String content = objectMapper.writeValueAsString(model);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(resultMatcher)
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    public String sendGet(String url, ResultMatcher resultMatcher) throws Exception {
        MvcResult result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(resultMatcher)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    public String sendPut(String url, ResultMatcher resultMatcher, Object model) throws Exception {
        String content = objectMapper.writeValueAsString(model);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .put(url)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(resultMatcher)
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    public EasyRandom getEasyRandom() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        EasyRandomParameters.Range<Integer> listSize = new EasyRandomParameters.Range<>(2, 2);
        EasyRandomParameters.Range<Integer> stringLength = new EasyRandomParameters.Range<>(6, 20);
        parameters.setCollectionSizeRange(listSize);
        parameters.setStringLengthRange(stringLength);
        return new EasyRandom(parameters);
    }
}
