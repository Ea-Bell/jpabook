package jpql;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;



public class JpqlMain {

	public static void main(String[] args)  {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    try {   
    	
        ObjectOrientedQueryLanguage_10(em, emf);		
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

		Jpql(em, emf);
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
		 */
		
		/**
		 * JPQL문법
		 * 	select m form Member as m where m.age>18
		 *  엔티티와 속성은 대소문자 구분O(Member, age)
		 *  JPQL키워드는 대소문자 구분 x(select, from , where)
		 *  엔티티 이름 사용, 테이블 이름 아님(Member)
		 *  별칭은 필수(m)(as는 생략 가능)
		 */
		
		
		Member member = new Member();
		member.setUsername("member1");
		member.setAge(10);
		em.persist(member);		
		
		
//		List<Member>result=em.createQuery("select m From Member m where m.name like '%kim%'",Member.class)
//							  .getResultList();
//		
//		
//		for(Member members : result) {
//			System.out.println("member = "+ members);
//		}
		
		TypeQueryAndQuery(em, emf, member);
		ParamiterBinding(em,emf, member);

		
		
		
		
		
	}


	


	private static void ParamiterBinding(EntityManager em, EntityManagerFactory emf, Member member) {
		/**
		 * 파라미터 바인딩- 이름 기준, 위치 기준.
		 */
		TypedQuery<Member> query = em.createQuery("select m from Member m where m.username=:username", Member.class);
		query.setParameter("username", "member1");
		Member singleResult = query.getSingleResult();
		System.out.println("singleResult = "+ singleResult);
		
	}

	private static void TypeQueryAndQuery(EntityManager em, EntityManagerFactory emf, Member member) {
		/*
		 * TypeQuery: 반환 타입이 명확할 때 사용
		 * Query: 반환 타입이 명확하지 않을 때 사용
		 */
		TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
		TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
		Query query3 = em.createQuery("select m.username, m.age from Member m", Member.class);
		
	}

	
}
