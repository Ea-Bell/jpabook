package dialect;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;


/**
 * 이거 바로 모르겠으면 자바 ORM 표준 JPA프로그래밍 -기본편 JPQL함수 9:18초 부터 registerFunction의 등록 하는법이 나온다.
 * <property name ="hibernate.dialect" value="내가 등록할 Dialect"위치/> << 함수 등록할때 class 위치에있는걸로 등록해야한다. 이유: extends해서...
 */
public class MyH2Dialect extends H2Dialect{

    public MyH2Dialect() {
        registerFunction("group_concat", new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
    }
    
}
