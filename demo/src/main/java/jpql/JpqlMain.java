package jpql;

import java.util.List;

import javax.naming.spi.DirStateFactory.Result;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class JpqlMain {

	public static void main(String[] args) {
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
		 * JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 플러시 플러시시점은 = commit과 sql문이 날라갈때 flush()가
		 * 나온다고함.
		 */

//		Jpql(em, emf);
//		QueryDSL(em, emf);//<<<이게 목표다.
//		Paging(em, emf);
//		Joins(em, emf);
		SubQuery(em, emf);
		
	}

	private static void SubQuery(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 서브 쿼리
		 *  나이가 평균보다 많은 회원
		 * 	 select m from Member m  where m.age>(select avg(m2.age)from Member m2)
		 * 
		 *  한 건이라도 주문한 고객
		 * 	 select m from Member m where(select count(o) from Order o hwere m = o.member)>0
		 */

		 /**
		  * [NOT]EXISTS (subquery): 서브쿼리에 결과가 존재하면 참
		  * {ALL | ANY | SOME} (subquery)
		  *	ALL : 모두 만족하면 참
		  * ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참
		  * [NOT] IN(subquery): 사브쿼리의 결과 중 하나라도 같은 것이 있으면 참
		  */

		  /**
		   *  서브쿼리 -예제
		   * 
		   * 팀A 소속인 회원
		   * select m from Member m
		   * 	where exists(select t from m.team t where t.name ='팀A')
		   * 
		   * 전체 상품 각각의 재고보다 주문량이 많은 주문들
		   * select o from Order o
		   * 	where o.orderAmount >ALL(select p.stockAmount from Product p)
		   * 
		   * 어떤 팀이든 팀에 소속된 회원
		   * select m from Member m
		   * 	where m.team =ANY(select t from Team t)
		   */

		   /**
			* JPA 서브 쿼리 한계
			*  JPA는 where, having 절에서만 서브 쿼리 사용 가능
			*  select 절도 가능(하이버 네이트에서 지원)
			*  from 절의 서부ㅡ 쿼리는 현재 JPQL에서 불가능  >> 중요!
			*    조인으로 풀 수 있으면 풀어서 해결
			* */

	}

	private static void Joins(EntityManager em, EntityManagerFactory emf) {


		Team team= new Team();
		team.setName("teamA");
		
		em.persist(team);

		Member member = new Member();
		member.setAge(10);
		member.setUsername("teamA");
		member.setTeam(team);
		em.persist(member);
		em.flush();
		em.clear();

		/**
		 * 조인
		 * 	내부 조인:
		 *   select m from Member m [Inner] join m.team t
		 *  외부 조인:
		 *   select m from Member m Left [Outer] Join m.team
		 * 	세타 조인:(연관 관계 없을때 필요한 조인)
		 *   select count(m) from Member m , Team t where m.username=t.name
		 */
		//Join(em, emf);


		/**
		 *  조인 on절
		 * 	 1. 조인 대상 필터링
		 *   2. 연관관계 없는 엔티티 외부 조인(하이버네이트 5.1부터)
		 * 
		 *  1_1 조인 대상 필터링
		 *   예) 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
		 * 	  JPQL:
		 * 		select m, t from Member m left join m.team t on t.name='A'
		 * 	  SQL:
		 * 		selecrt m.*, t.* from
		 * 		Member m left join Team t on m.TEAM_ID=t.id and t.name='A'
		 * 	
		 *  2_1 연관관계 없는 엔티티 외부 조인
		 * 	 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
		 * 	  JPQL:
		 * 		select m, t from Member m left join Team t on m.username=t.name
		 * 	  SQL:
		 * 		select m.*, t.* from Member m Left join Team t on m.username= t.name
		 * 
		 */
			

		//조인 대상 필터링
		//JoinFilter(em, emf);
			
		 //연관 관계 없는 대상
		 NoAssociation(em, emf);

 		 
	}

	private static void Join(EntityManager em, EntityManagerFactory emf) {
		String innerJoin = "select m from Member m inner join m.team t ";
		String innerJoin2 = "select m from Member m join m.team t ";
		String leftOuterJoin = "select m from Member m left outer join m.team t";
		String leftOuterJoin2 = "select m from Member m left join m.team t";
		String setaJoin = "select m from Member m , Team t where m.username=t.name"; //cross라고 나옴!
		List<Member> result = em.createQuery(setaJoin, Member.class)
							.getResultList();
		
		System.out.println("result = " + result.size());
	}

	private static void NoAssociation(EntityManager em, EntityManagerFactory emf) {
		String noAssociation= "select m from Member m left join Team t on m.username=t.name";
		List<Member> onQueryResult = em.createQuery(noAssociation, Member.class)
		.getResultList();
		System.out.println("result :"+ onQueryResult.size());
	}

	private static void JoinFilter(EntityManager em, EntityManagerFactory emf) {
		String onQuery= "select m from Member m left join m.team t on t.name= 'teamA'";
		List<Member> onQueryResult = em.createQuery(onQuery, Member.class)
		.getResultList();
		System.out.println("result :"+ onQueryResult.size());
	}

	private static void Paging(EntityManager em, EntityManagerFactory emf) {
			/**
			 * 페이징 API
			 * JPA는 페이징을 다음 두 API로 추상화
			 * setFirstResult(int start Position): 조회 시작 위치(0부터 시작)
			 * setMaxResults(int maxResult): 조회할 데이터 수
			 * 
			 * 현재 데이터 베이스 방언으로 돌아간다. persistence.xml의 hibernate.dialect의 value값을 바꾸면 각 DB로 맞춰서 쿠리를 짜서 보낸다.
			 */
			
			for(int i= 0; i<100; i++){
			 Member member = new Member();
			 member.setUsername("member"+i);
			 member.setAge(i);
			 em.persist(member);
			}
			 em.flush();
			 em.clear();

			List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
						.setFirstResult(1)
						.setMaxResults(10)
						.getResultList();

				
			System.out.println("result.size : "+result.size());

			for (Member member1: result){
				System.out.println("member1 =" + member1);
			}



	}

	private static void QueryDSL(EntityManager em, EntityManagerFactory emf) {

		/**
		 * QueryDSL 문자가 아닌 자바코드로 JPQL을 작성할 수 있음 JPQL빌더 역할 컴파일 시점에 문법 오류를 찾을 수 있음 동적쿼리 작성
		 * 편리함 단순하고 쉬움 실무 사용권장!
		 * http://querydsl.com/static/querydsl/5.0.0/reference/html_single/#jpa_integration
		 */

	}

	private static void Jpql(EntityManager em, EntityManagerFactory emf) {
		/**
		 * JPQL 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리 SQL을 추상화해서 특정 데이터베이스 SQL에 의존X JPQL을 한마디로
		 * 정의하면 객체 지향 SQL
		 */

		/**
		 * JPQL문법 select m form Member as m where m.age>18 엔티티와 속성은 대소문자 구분O(Member,
		 * age) JPQL키워드는 대소문자 구분 x(select, from , where) 엔티티 이름 사용, 테이블 이름 아님(Member)
		 * 별칭은 필수(m)(as는 생략 가능)
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

//		TypeQueryAndQuery(em, emf, member);
//		ParamiterBinding(em,emf, member);
		Projection(em, emf, member);

	}

	private static void Projection(EntityManager em, EntityManagerFactory emf, Member member) {
		/**
		 * 프로젝션 select 절에 조회할 대상을 지정하는 것 프로젝션 대상: 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자등 기본 데이터
		 * 타입) select m from Member m ->엔티티 프로젝션 select m.team from Member m -> 엔티티 프로젝션
		 * select m.address from Member m -> 임베디드 타입 프로젝션 select m.username, m.age from
		 * Member m -> 스칼라 타입 프로젝션 DISTINCT로 중복 제거
		 */

		/**
		 * 
		 */

		/*
		 * 이렇게 묵시적으로 적으면 안된다 이유: 다른 사람이 Join이 쓰는지 안쓰는지 모르기 때문. List<Team> result =
		 * em.createQuery("select m.team from Member m", Team.class).getResultList();
		 */

		// List<Team> result = em.createQuery("select t from Member m Join m.team
		// t",Team.class).getResultList();//<<이렇게 명시적으로 적어야함.

		// em.createQuery("select o.address from Order o",
		// Address.class).getResultList();

		// em.createQuery("select distinct m.username, m.age from Member
		// m").getResultList(); //distinct

		ProjectionMulitValueSelect(em, emf, member);

	}

	private static void ProjectionMulitValueSelect(EntityManager em, EntityManagerFactory emf, Member member) {
		/**
		 * 여러값 조회
		 * Select m.username, m.age from Member m
		 * 1. Query 타입으로 조회
		 * 2. Object[]타입으로 조회
		 * 3. new 명령어로 조회
		 *  단순 값을 DTO로 바로 조회
		 *  select new jpabook.jpql.UserDTO(m.username, m.age)from Member m
		 *  패키지명을 포함한 전체 클래스 명 입력
		 *  순서와 타입이 일치하는 생성자 필요
		 */
		
		/**
		 * Query 타입으로 조회
		 */
//		List resultList = em.createQuery("select m.username, m.age from Member m")
//									.getResultList();
//		Object o = resultList.get(0);
//		Object [] result = (Object[])o;
//		
//		System.out.println("username = "+ result[0]);
//		System.out.println("result = "+ result[1]);

		/**
		 * Objet[] 타입으로 조회
		 */
//		List<Object[]> resultList2 = em.createQuery("select m.username, m.age from Member m").getResultList();
//		
//		Object[] result2 = resultList2.get(0);
//	
//		System.out.println("username = "+ result2[0]);
//		System.out.println("result = "+ result2[1]);
		
		
		/**
		 * new 타입으로 조회
		 */

		
			
		
		
		em.createQuery("select new jpql.MemberDTO(m.username, m.age)from Member m", MemberDTO.class);
	}


	private static void ParamiterBinding(EntityManager em, EntityManagerFactory emf, Member member) {
		/**
		 * 파라미터 바인딩- 이름 기준, 위치 기준.
		 * 
		 */
		TypedQuery<Member> query = em.createQuery("select m from Member m where m.username=:username", Member.class);
		query.setParameter("username", "member1");
		Member singleResult = query.getSingleResult();
		System.out.println("singleResult = " + singleResult);

		
	}

	private static void TypeQueryAndQuery(EntityManager em, EntityManagerFactory emf, Member member) {
		/*
		 * TypeQuery: 반환 타입이 명확할 때 사용 Query: 반환 타입이 명확하지 않을 때 사용
		 */
		TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
		TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
		Query query3 = em.createQuery("select m.username, m.age from Member m", Member.class);

	}
}
