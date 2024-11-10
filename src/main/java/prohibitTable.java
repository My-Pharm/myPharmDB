import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.sql.*;
import java.util.Properties;

public class prohibitTable {
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
        int count=0;

        try {
            // MySQL JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 데이터베이스 연결
            conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
            System.out.println("Database connection: " + (conn != null ? "Successful" : "Failed"));

            // JSON 파일을 읽기
            JSONParser parser = new JSONParser();
            Reader reader = new FileReader("src/main/resources/prohibit_filtered2.json");
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            System.out.println("json파일 불러옴");

            String sqlProhibitTable = "INSERT INTO prohibit_entity(ingredient_name1,ingredient_name2) VALUES (?,?)";
            //pstmt = conn.prepareStatement(sqlAleartTable,Statement.RETURN_GENERATED_KEYS);

            // 트랜잭션 수동 커밋

            // JSON 배열 순회
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                pstmt = conn.prepareStatement(sqlProhibitTable, Statement.RETURN_GENERATED_KEYS);
                // ITEM_NAME만 추출
                String INGR_KOR_NAME = (String) jsonObject.get("INGR_KOR_NAME");
                String MIXTURE_INGR_KOR_NAME = (String) jsonObject.get("MIXTURE_INGR_KOR_NAME");

                pstmt.setString(1, MIXTURE_INGR_KOR_NAME);
                pstmt.setString(2, INGR_KOR_NAME);

                try {
                    // 데이터베이스에 삽입
                    pstmt.executeUpdate();

                } catch (SQLIntegrityConstraintViolationException e) {
                    // 중복 항목이 있을 경우 예외를 처리하고 넘어감

                }
            }

            // 트랜잭션 커밋

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.out.println("예외발생");
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
    }
}
