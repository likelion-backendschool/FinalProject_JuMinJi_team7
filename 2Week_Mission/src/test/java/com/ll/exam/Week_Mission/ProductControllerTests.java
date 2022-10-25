package com.ll.exam.Week_Mission;

import com.ll.exam.Week_Mission.app.post.domain.hashtag.entity.PostHashTag;
import com.ll.exam.Week_Mission.app.post.domain.hashtag.service.PostHashTagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ProductControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private PostHashTagService postHashTagService;

    @Test
    @DisplayName("멤버 아이디로 post 해시태그 가져오기")
    void t1() throws Exception {
       List<PostHashTag> list = postHashTagService.findByMemberId(2L);

      assertThat(list.get(0).getId()).isEqualTo(7);
      assertThat(list.size()).isEqualTo(14);
      // assertThat(list.size()).isEqualTo(10); // expected: false

    }
}
