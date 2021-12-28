package jpql;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;



public class JpqlMain {

	public static void main(String[] args)  {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    try {   
    	
//        ObjectOrientedQueryLanguage_10(em, emf);
//        
		Member member = new Member();
		member.setUsername("member1");
		em.persist(member);
		
        tx.commit();
    } catch (Exception e) {
        tx.rollback();
        e.printStackTrace();
    } finally {
        em.close();
    }
    emf.close();
}

	
	private static void ObjectOrientedQueryLanguage_10(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 
		 * JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 플러시
		 * 플러시시점은 = commit과 sql문이 날라갈때 flush()가 나온다고함.
		 */
		

	
		
		
		
//		Jpql(em, emf);
//		QueryDSL(em, emf);//<<<이게 목표다.
	}

	private static void QueryDSL(EntityManager em, EntityManagerFactory emf) {
		
		/**
		 * QueryDSL 
		 *  문자가 아닌 자바코드로 JPQL을 작성할 수 있음
		 *  JPQL빌더 역할
		 *  컴파일 시점에 문법 오류를 찾을 수 있음
		 *  동적쿼리 작성 편리함
		 *  단순하고 쉬움
		 *  실무 사용권장!
		 *  http://querydsl.com/static/querydsl/5.0.0/reference/html_single/#jpa_integration
		 */
		
	}

	private static void Jpql(EntityManager em, EntityManagerFactory emf) {
		/**
		 * JPQL
		 *  테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
		 *  SQL을 추상화해서 특정 데이터베이스 SQL에 의존X
		 *  JPQL을 한마디로 정의하면 객체 지향 SQL
		 *  
		 *  
		 */
		List<Member>result=em.createQuery("select m From Member m where m.name like '%kim%'",Member.class).getResultList();
		for(Member member : result) {
			System.out.println("member = "+ member);
		}
		
	}
	
}
