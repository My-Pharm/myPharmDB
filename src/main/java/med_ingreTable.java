
import java.io.FileInputStream;
import java.sql.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;

public class med_ingreTable {

    @Value("${url}")
    private static String jdbcUrl;
    @Value("${username}")
    private static String jdbcUser;
    @Value("${password}")
    private static String jdbcPassword;

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/application.properties"));
        jdbcUrl = properties.getProperty("url");
        jdbcUser = properties.getProperty("username");
        jdbcPassword = properties.getProperty("password");

        try {
            // MySQL JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 데이터베이스 연결
            conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
            System.out.println("Database connection: " + (conn != null ? "Successful" : "Failed"));

            // JSON 파일을 읽기
            JSONParser parser = new JSONParser();
            Reader reader = new FileReader("src/main/resources/drugDataFilter.json");
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            System.out.println("json파일 불러옴");

            String sqlMedicineTable = "INSERT INTO medicine_entity(medicine_name) VALUES (?)";
            pstmt = conn.prepareStatement(sqlMedicineTable);

            // 트랜잭션 수동 커밋

            // JSON 배열 순회
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;

                // ITEM_NAME만 추출
                String ITEM_NAME = (String) jsonObject.get("ITEM_NAME");
                pstmt.setString(1, ITEM_NAME);

                try {
                    // 데이터베이스에 삽입
                    System.out.println(ITEM_NAME);
                    pstmt.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException e) {
                    // 중복 항목이 있을 경우 예외를 처리하고 넘어감
                    System.out.println("Duplicate entry, skipping: " + ITEM_NAME);
                }
            }

            // 트랜잭션 커밋

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // 예외 발생 시 롤백
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
//        try {
//            // MySQL JDBC 드라이버 로드
//            Class.forName("com.mysql.cj.jdbc.Driver");
//
//            // 데이터베이스 연결
//            conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
//            System.out.println("Database connection: " + (conn != null ? "Successful" : "Failed"));
//
//            // JSON 파일을 읽기
//            JSONParser parser = new JSONParser();
//            Reader reader = new FileReader("src/main/resources/drugDataFilter.json");
//            JSONArray jsonArray = (JSONArray) parser.parse(reader);
//            System.out.println("json파일 불러옴");
//
//            String sqlMedicineTable = "INSERT INTO medicine_entity(medicine_name) VALUES (?)";
//            pstmt = conn.prepareStatement(sqlMedicineTable);
//
//            // 트랜잭션 수동 커밋 설정
//            conn.setAutoCommit(false);
//
//            // JSON 배열 순회
//            int batchSize = 100;
//            int count = 0;
//
//            for (Object obj : jsonArray) {
//                JSONObject jsonObject = (JSONObject) obj;
//
//                // ITEM_NAME만 추출
//                String ITEM_NAME = (String) jsonObject.get("ITEM_NAME");
//                pstmt.setString(1, ITEM_NAME);
//
//                try {
//                    // 배치에 추가
//                    pstmt.addBatch();
//                    count++;
//
//                    // 100개마다 실행
//                    if (count % batchSize == 0) {
//                        pstmt.executeBatch();
//                        conn.commit(); // 커밋하여 데이터베이스에 저장
//                        System.out.println("Inserted batch of " + batchSize);
//                    }
//                } catch (SQLIntegrityConstraintViolationException e) {
//                    // 중복 항목이 있을 경우 예외를 처리하고 넘어감
//                    System.out.println("Duplicate entry, skipping: " + ITEM_NAME);
//                }
//            }
//
//            // 남아 있는 배치 실행 및 커밋
//            if (count % batchSize != 0) {
//                pstmt.executeBatch();
//                conn.commit();
//                System.out.println("Inserted remaining records.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (conn != null) {
//                try {
//                    conn.rollback(); // 예외 발생 시 롤백
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        } finally {
//            try {
//                if (pstmt != null) pstmt.close();
//                if (conn != null) conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            // MySQL JDBC 드라이버 로드
//            Class.forName("com.mysql.cj.jdbc.Driver");
//
//            // 데이터베이스 연결
//            conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
//            System.out.println("Database connection: " + (conn != null ? "Successful" : "Failed"));
//
//            // JSON 파일을 읽기
//            JSONParser parser = new JSONParser();
//            Reader reader = new FileReader("src/main/resources/drugDataFilter.json");
//            JSONArray jsonArray = (JSONArray) parser.parse(reader);
//            System.out.println("JSON 파일 불러옴");
//
//            String sqlMedicineTable = "INSERT INTO medicine_entity(medicine_name) VALUES (?)";
//            pstmt = conn.prepareStatement(sqlMedicineTable);
//
//            // 100개씩 배치 삽입
//            int count = 0;
//
//            for (Object obj : jsonArray) {
//                JSONObject jsonObject = (JSONObject) obj;
//                String itemName = (String) jsonObject.get("ITEM_NAME");
//
//                pstmt.setString(1, itemName);
//                pstmt.addBatch();
//                count++;
//

//                // 100개마다 배치 실행
//                if (count % 10 == 0) {
//                    try {
//                        System.out.println(count);
//                        pstmt.executeBatch();
//                    } catch (BatchUpdateException e) {
//                        // 실패한 항목을 기록
//
//                    }
//                    pstmt.clearBatch(); // 배치 초기화
//                }
//            }
//
//            // 남은 항목 배치 실행
//            try {
//                pstmt.executeBatch();
//            } catch (BatchUpdateException e) {
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (conn != null) {
//                try {
//                    conn.rollback(); // 예외 발생 시 롤백
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        } finally {
//            try {
//                if (pstmt != null) pstmt.close();
//                if (conn != null) conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
