package jpql;

import java.util.Collection;
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

			// ObjectOrientedQueryLanguage_10(em, emf);
			ObjectOrientedQueryLanguage_MiddleGrammar_11(em, emf);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
		emf.close();

	}

	private static void ObjectOrientedQueryLanguage_MiddleGrammar_11(EntityManager em, EntityManagerFactory emf) {

		경로표현식(em, emf);

	}

	private static void 경로표현식(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 실무 조언
		 *  가급적 무시적 조인 대신에 명시적 조인 사용
		 *  조인은 sql 튜닝에 중요 포인트
		 *  묵시적 조인은 조인이 일어나는 상황을 하눈ㄴ에 파악하기 어렵다.
		 */
		
		
		/**
		 * 명시적 조인, 묵시적 조인
		 *  명시적 조인: join 키워드 직접 사용
		 *   select m from Member m join m.team t
		 *  묵시적 조인: 경로 표현식에 의해 묵시적으로 SQL조인 발생 (내부 조인만 가능)
		 * 
		 * select m.team from Member m
		 */

		/**
		 *  절대로 묵시적 조인 쓰지 말것.!!!!!!!!!!!!!!!!!!!!
		 * 경로 표현식
		 * .(점)을 찍어 객체 그래프를 탐색하는 것.
		 * select m.username -> 상태 필드
		 * from Member m
		 * join m.team t -> 단일 값 연관 필드
		 * join m.orders o -> 컬렉션 값 연관 필드
		 * where t.name ='팀A'
		 * 
		 * 경로 표현식 용어 정리
		 * 상태 필드(state field): eㅏㄴ순히 값을 저장하기 위한 필드 (ex: m.username)
		 * 연관 필드(association field): 연관관계를 위한 필드
		 * 단일 값 연관 필드
		 * @ManyToOne, @OneToOne, 대상이 엔티티(Ex: m.team)
		 * 컬렉션 값 연관 필드
		 * @OneToMany, @ManyToMany, 대상이 컬렉션(Ex:m.orders)
		 * 
		 * 경로 표현식 특징
		 * 상태 필드(state field): 경로 탐색의 끝, 탐색 X
		 * 단일 값 연관 경로: 묵시적 내부 조인(inner join) 발생, 탐색 O
		 * 컬렉션 값 연관 경로: 묵시적 내부 조인 발생, 탐색 X
		 * from 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능
		 */

		 /**
		  * 경로 표현식 -예제
		  * select o.member.team from Order o -> 성공 ( 실행시 쿼리가 쉽게 예상하지 못한다.
		  *
		  * select t.members from Team -> 성공
		  *
		  * select t.members.username from Team t-> 실패
		  *
		  * select m.username from Team t join t.members m -> 성공 명시적조인
		  */

		  /**
		   * 경로 탐색을 사용한 묵시적 조인시 주의사항
		   *  항상 내부 조인
		   *  컬렉션은 경로 탐색의 끝, 명시적 조인을 통해 별칭을 얻어야함.
		   *  경로 탐색은 주로 select, where 절에서 사용하지만 묵시적 조인으으로 인해 SQL의 FROM(JOIN)절에 영향을 줌.
		   */

		Team team = new Team();
		em.persist(team);

		Member member1 = new Member();
		member1.setUsername("관리자1");
		member1.setTeam(team);
		em.persist(member1);
		Member member2 = new Member();
		member2.setTeam(team);
		member2.setUsername("관리자2");

		em.persist(member2);

		em.flush();
		em.clear();

		// StateField(em, emf);
		// SingleValuePaht(em, emf);
		CollectionValuePath(em, emf);

	}

	private static void CollectionValuePath(EntityManager em, EntityManagerFactory emf) {
		// String query = "select t.member from Team t";  //컬렉션 타입이라서 object로 받아야한다. 그런데 이렇게 받으면 안됨.
		

		// List<Collection> resultList = em.createQuery(query, Collection.class).getResultList();

		// for (Object team : resultList) {
		// 	System.out.println("team : "+ team);
		// }


		//이렇게 명확하게 줘야 알아먹기 편하니! 반드시 잊어버리지 말고 이렇게 써야함.
		String explicitQuery = "select m from Team t join  t.member m";
			List<Member> explicitResultList = em.createQuery(explicitQuery, Member.class).getResultList();

			for (Member team : explicitResultList) {
				System.out.println("team : "+ team);
			}
	}

	private static void SingleValuePaht(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 묵시적 내부조인
		 * 실제로 사용할때는 묵시적 내부조인을 사용하면 안된다.
		 * query튜닝할때 문제가 생기는 경우가 많아진다.
		 * JPQL이랑 sql이랑 문법이 비슷하게 적는게 좋다.
		 */
		String query = "select m.team from Member m";
		List<Team> resultList = em.createQuery(query, Team.class).getResultList();
		for (Team team : resultList) {
			System.out.println("s = " + team);
		}
	}

	private static void StateField(EntityManager em, EntityManagerFactory emf) {

		String query = "select m.username from Member m";
		List<String> resultList = em.createQuery(query, String.class).getResultList();

		for (String string : resultList) {
			System.out.println("s = " + string);
		}

	}

	private static void ObjectOrientedQueryLanguage_10(EntityManager em, EntityManagerFactory emf) {
		/**
		 * JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 플러시 플러시시점은 = commit과 sql문이 날라갈때 flush()가
		 * 나온다고함.
		 */

		// Jpql(em, emf);
		// QueryDSL(em, emf);//<<<이게 목표다.
		// Paging(em, emf);
		// Joins(em, emf);
		// SubQuery(em, emf);
		// JpqlTypeRepresent(em, emf);
		// ConditionalRepresent(em, emf);
		JpqlBaseFunction(em, emf);
	}

	private static void JpqlBaseFunction(EntityManager em, EntityManagerFactory emf) {
		/**
		 * JPQL 기본 함수
		 * CONCAT 문자열 더하기
		 * SUBSTRING 문자열을 x번 부터 x번 까지 짜르기.
		 * TRIM
		 * LOWER, UPPER
		 * LENGTH
		 * LOCATE
		 * ABS, SQRT, MOD
		 * SIZE, INDEX(JPA 용도)
		 */

		/**
		 * 사용자 정의 함수 호출
		 * 하이버네이트는 사용전 방언에 추가해야 한다.
		 * 사용하는 DB방언을 상속 받고, 사용자 저으이 함수를 등록한다.
		 * select function ('group_concat', i.name) from Item i
		 */

		// BaseFunction(em, emf);
		UserDefinedFunction(em, emf);
	}

	private static void UserDefinedFunction(EntityManager em, EntityManagerFactory emf) {
		// 각 DB에 선언된 사용자 정의 함수를 불러오는 거임!!! 만약 사용할꺼면 DB에 사용자 정의 함수 만들어서 불러와!!!!

		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member1 = new Member();
		member1.setUsername("관리자1");

		em.persist(member1);
		Member member2 = new Member();
		member2.setUsername("관리자2");

		em.persist(member2);

		em.flush();
		em.clear();

		String userFunction = "select group_concat(m.username) from Member m";
		List<String> resultList = em.createQuery(userFunction, String.class)
				.getResultList();
		for (String string : resultList) {
			System.out.println("s : " + string);
		}
	}

	private static void BaseFunction(EntityManager em, EntityManagerFactory emf) {
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member = new Member();
		member.setUsername("관리자");
		member.setAge(10);
		member.setType(MemberType.ADMIN);

		em.persist(member);

		em.flush();
		em.clear();

		String conCatQuery = "select concat ('a','b') from Member m";
		String subStringQuery = "select substring(m.username, 2,3) from Member m";

		List<String> resultList = em.createQuery(subStringQuery, String.class).getResultList();
		// for (String string : resultList) {
		// System.out.println("s = " + string);
		// }

		/// 밑에는 interger형들.
		String locateQuery = "select locate('de','abcdefg') from Member m"; // >>반환값은 int로 위치값을 반환해준다.
		String sizeQuery = "select size(t.member) from Team t";
		List<Integer> intResultList = em.createQuery(sizeQuery, Integer.class).getResultList();
		for (Integer string : intResultList) {
			System.out.println("s = " + string);
		}

	}

	private static void ConditionalRepresent(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 조건식 -case 식
		 * 
		 * 기본 case 식
		 * select
		 * case when m.age <= 10 then '학생요금'
		 * when m.age >= 60 then '경로요금'
		 * else '일반요금'
		 * end
		 * from Member m
		 * 
		 * 단순 case 식
		 * select
		 * case t.name
		 * when '팀A' then '인센티브110%'
		 * when '팀B' then '인센티브120%'
		 * else '인센티브 105%'
		 * end
		 * from Team t
		 * 
		 * COALESCE: 하나씩 조회해서 nulldㅣ 아니면 반환
		 * 사용자 이름이 없으면 이름 없는 회원을 반환
		 * select coalesce(m.username, '이름 없는 회원') from Member m
		 * 
		 * NUllIF: 두 값이 같으면 null 반환, 다르면 첫번쨰 값 반환
		 * 사용자 이름이 '관리자'면 null을 반환하고 나머지는 본인의 이름을 반환
		 * select nullif(m.username, '관리자') from Member m
		 */

		// BaseIf(em, emf);
		// Coalesce(em, emf);
		Nullif(em, emf);

	}

	private static void Nullif(EntityManager em, EntityManagerFactory emf) {
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member = new Member();
		member.setUsername("관리자");
		member.setAge(10);
		member.setType(MemberType.ADMIN);

		em.persist(member);

		em.flush();
		em.clear();

		// 무언가를 숨길 때 nullif가 쓰인다.
		String query = "select nullif(m.username, '관리자') as username" +
				" from Member m";
		List<String> resultList = em.createQuery(query, String.class).getResultList();

		for (String string : resultList) {
			System.out.println("s = " + string);
		}
	}

	private static void Coalesce(EntityManager em, EntityManagerFactory emf) {
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member = new Member();
		member.setUsername(null);
		member.setAge(10);
		member.setType(MemberType.ADMIN);

		em.persist(member);

		em.flush();
		em.clear();

		String query = "select coalesce(m.username, '이름 없는 회원') as username from Member m";

		List<String> resultList = em.createQuery(query, String.class).getResultList();
		for (String string : resultList) {
			System.out.println("s = " + string);
		}
	}

	private static void BaseIf(EntityManager em, EntityManagerFactory emf) {
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member = new Member();
		member.setUsername("member1");
		member.setAge(10);
		member.setType(MemberType.ADMIN);

		em.persist(member);

		em.flush();
		em.clear();

		String query = "select " +
				"case when m.age <= 10 then '학생요금'" +
				"	  when m.age >= 60 then '경로요금'" +
				"	  else '일반요금' " +
				" end " +
				" from Member m";
		List<String> resultList = em.createQuery(query, String.class).getResultList();

		for (String s : resultList) {
			System.out.println("s = " + s);
		}

	}

	private static void JpqlTypeRepresent(EntityManager em, EntityManagerFactory emf) {
		/**
		 * JPQL 타입 표현
		 * 문자: 'HELLO', 'She''s'
		 * 숫자: 10L(Long), 10D(Double), 10F(Float)
		 * Boolean: TRUE, FALSE
		 * ENUM: jpabook.MemberType.Admin(패키지명 포함)
		 * 엔티티 타입: TYPE(m) = Member(상속 관계에서 사용)
		 */

		Team team = new Team();
		team.setName("teamA");
		em.persist(team);

		Member member = new Member();
		member.setUsername("member1");
		member.setAge(10);
		member.setType(MemberType.ADMIN);

		em.persist(member);

		em.flush();
		em.clear();

		String query = "select m.username, 'HELLO', TRUE FROM Member m " +
				"where m.type= jpql.MemberType.ADMIN";
		String errorQuery = "select m.username, 'HELLO', TRUE FROM Member m " +
				"where m.type= jpql.MemberType.USER";

		List<Object[]> result = em.createQuery(errorQuery).getResultList();

		// for(Object[] objects: result){
		// System.out.println("object = " + objects[0]);
		// System.out.println("object = " + objects[1]);
		// System.out.println("object = " + objects[2]);

		// }

		// enumType은 이렇게 씁니다.
		String paramBind = "select m.username, 'HELLO', TRUE FROM Member m " +
				"where m.type= :userType";
		List<Object[]> enumTypeList = em.createQuery(paramBind).setParameter("userType", MemberType.ADMIN)
				.getResultList();

		for (Object[] objects : enumTypeList) {
			System.out.println("object = " + objects[0]);
			System.out.println("object = " + objects[1]);
			System.out.println("object = " + objects[2]);

		}

		/**
		 * JPQL 기타
		 * SQL과 문법이 같은식
		 * EXISTS, IN
		 * AND, OR NOT
		 * =, >, >=, <, <=, <>
		 * BETWEEN, LIKE, IS NULL
		 */

		String jpqlEx = "select m.username, 'HELLO', TRUE FROM Member m " +
				"where m.age between 0 and 10";
		List<Object[]> jpqlExList = em.createQuery(paramBind).setParameter("userType", MemberType.ADMIN)
				.getResultList();

		for (Object[] objects : jpqlExList) {
			System.out.println("object = " + objects[0]);
			System.out.println("object = " + objects[1]);
			System.out.println("object = " + objects[2]);

		}

	}

	private static void SubQuery(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 서브 쿼리
		 * 나이가 평균보다 많은 회원
		 * select m from Member m where m.age>(select avg(m2.age)from Member m2)
		 * 
		 * 한 건이라도 주문한 고객
		 * select m from Member m where(select count(o) from Order o hwere m =
		 * o.member)>0
		 */

		/**
		 * [NOT]EXISTS (subquery): 서브쿼리에 결과가 존재하면 참
		 * {ALL | ANY | SOME} (subquery)
		 * ALL : 모두 만족하면 참
		 * ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참
		 * [NOT] IN(subquery): 사브쿼리의 결과 중 하나라도 같은 것이 있으면 참
		 */

		/**
		 * 서브쿼리 -예제
		 * 
		 * 팀A 소속인 회원
		 * select m from Member m
		 * where exists(select t from m.team t where t.name ='팀A')
		 * 
		 * 전체 상품 각각의 재고보다 주문량이 많은 주문들
		 * select o from Order o
		 * where o.orderAmount >ALL(select p.stockAmount from Product p)
		 * 
		 * 어떤 팀이든 팀에 소속된 회원
		 * select m from Member m
		 * where m.team =ANY(select t from Team t)
		 */

		/**
		 * JPA 서브 쿼리 한계
		 * JPA는 where, having 절에서만 서브 쿼리 사용 가능
		 * select 절도 가능(하이버 네이트에서 지원)
		 * from 절의 서브쿼리는 현재 JPQL에서 불가능 >> 중요!
		 * 조인으로 풀 수 있으면 풀어서 해결
		 */

	}

	private static void Joins(EntityManager em, EntityManagerFactory emf) {

		Team team = new Team();
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
		 * 내부 조인:
		 * select m from Member m [Inner] join m.team t
		 * 외부 조인:
		 * select m from Member m Left [Outer] Join m.team
		 * 세타 조인:(연관 관계 없을때 필요한 조인)
		 * select count(m) from Member m , Team t where m.username=t.name
		 */
		// Join(em, emf);

		/**
		 * 조인 on절
		 * 1. 조인 대상 필터링
		 * 2. 연관관계 없는 엔티티 외부 조인(하이버네이트 5.1부터)
		 * 
		 * 1_1 조인 대상 필터링
		 * 예) 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
		 * JPQL:
		 * select m, t from Member m left join m.team t on t.name='A'
		 * SQL:
		 * selecrt m.*, t.* from
		 * Member m left join Team t on m.TEAM_ID=t.id and t.name='A'
		 * 
		 * 2_1 연관관계 없는 엔티티 외부 조인
		 * 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
		 * JPQL:
		 * select m, t from Member m left join Team t on m.username=t.name
		 * SQL:
		 * select m.*, t.* from Member m Left join Team t on m.username= t.name
		 * 
		 */

		// 조인 대상 필터링
		// JoinFilter(em, emf);

		// 연관 관계 없는 대상
		// NoAssociation(em, emf);

	}

	private static void Join(EntityManager em, EntityManagerFactory emf) {
		String innerJoin = "select m from Member m inner join m.team t ";
		String innerJoin2 = "select m from Member m join m.team t ";
		String leftOuterJoin = "select m from Member m left outer join m.team t";
		String leftOuterJoin2 = "select m from Member m left join m.team t";
		String setaJoin = "select m from Member m , Team t where m.username=t.name"; // cross라고 나옴!
		List<Member> result = em.createQuery(setaJoin, Member.class)
				.getResultList();

		System.out.println("result = " + result.size());
	}

	private static void NoAssociation(EntityManager em, EntityManagerFactory emf) {
		String noAssociation = "select m from Member m left join Team t on m.username=t.name";
		List<Member> onQueryResult = em.createQuery(noAssociation, Member.class)
				.getResultList();
		System.out.println("result :" + onQueryResult.size());
	}

	private static void JoinFilter(EntityManager em, EntityManagerFactory emf) {
		String onQuery = "select m from Member m left join m.team t on t.name= 'teamA'";
		List<Member> onQueryResult = em.createQuery(onQuery, Member.class)
				.getResultList();
		System.out.println("result :" + onQueryResult.size());
	}

	private static void Paging(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 페이징 API
		 * JPA는 페이징을 다음 두 API로 추상화
		 * setFirstResult(int start Position): 조회 시작 위치(0부터 시작)
		 * setMaxResults(int maxResult): 조회할 데이터 수
		 * 
		 * 현재 데이터 베이스 방언으로 돌아간다. persistence.xml의 hibernate.dialect의 value값을 바꾸면 각 DB로
		 * 맞춰서 쿠리를 짜서 보낸다.
		 */

		for (int i = 0; i < 100; i++) {
			Member member = new Member();
			member.setUsername("member" + i);
			member.setAge(i);
			em.persist(member);
		}
		em.flush();
		em.clear();

		List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
				.setFirstResult(1)
				.setMaxResults(10)
				.getResultList();

		System.out.println("result.size : " + result.size());

		for (Member member1 : result) {
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

		// List<Member>result=em.createQuery("select m From Member m where m.name like
		// '%kim%'",Member.class)
		// .getResultList();
		//
		//
		// for(Member members : result) {
		// System.out.println("member = "+ members);
		// }

		// TypeQueryAndQuery(em, emf, member);
		// ParamiterBinding(em,emf, member);
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
		 * 단순 값을 DTO로 바로 조회
		 * select new jpabook.jpql.UserDTO(m.username, m.age)from Member m
		 * 패키지명을 포함한 전체 클래스 명 입력
		 * 순서와 타입이 일치하는 생성자 필요
		 */

		/**
		 * Query 타입으로 조회
		 */
		// List resultList = em.createQuery("select m.username, m.age from Member m")
		// .getResultList();
		// Object o = resultList.get(0);
		// Object [] result = (Object[])o;
		//
		// System.out.println("username = "+ result[0]);
		// System.out.println("result = "+ result[1]);

		/**
		 * Objet[] 타입으로 조회
		 */
		// List<Object[]> resultList2 = em.createQuery("select m.username, m.age from
		// Member m").getResultList();
		//
		// Object[] result2 = resultList2.get(0);
		//
		// System.out.println("username = "+ result2[0]);
		// System.out.println("result = "+ result2[1]);

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
