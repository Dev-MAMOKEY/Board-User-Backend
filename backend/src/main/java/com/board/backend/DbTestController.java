package com.board.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class DbTestController {

    private final DataSource dataSource;

    @GetMapping("/db")
    public String testDb() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            return "DB 연결 성공: " + conn.getMetaData().getURL();
        }
    }
}
