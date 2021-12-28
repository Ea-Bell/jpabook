package jpabook.jpashop;

import java.util.List;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Persistence;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.AddressEntity;
import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Child;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Parent;
import jpabook.jpashop.domain.Team;


public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {   
        	
        //ProxyAssociation_08(em, emf);
        //ValueType_09(em, emf);
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
		QueryDSL(em, emf);//<<<이게 목표다.
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
		 */
		
	}

	private static void Jpql(EntityManager em, EntityManagerFactory emf) {
		/**
		 * JPQL
		 *  테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
		 *  SQL을 추상화해서 특정 데이터베이스 SQL에 의존X
		 *  JPQL을 한마디로 정의하면 객체 지향 SQL
		 *  http://querydsl.com/static/querydsl/5.0.0/reference/html_single/#jpa_integration
		 *  
		 */
		List<Member>result=em.createQuery("select m From Member m where m.name like '%kim%'",Member.class).getResultList();
		for(Member member : result) {
			System.out.println("member = "+ member);
		}
		
	}

	/**
	 * 09 값 타입.
	 * 정리
	 *  값 타입은 정말 값 타입이라 판단될 때만 사용
	 *  엔티티와 값 타입을 혼동해서 엔티티를 값 타입으로 만들면 안됨
	 *  식별자가 필요하고, 지속해서 값을 추적, 변경해야 한다면 그것은 값 타입이 아닌 엔티티!(중요!)
	 */
    private static void ValueType_09(EntityManager em, EntityManagerFactory emf) {
		  	
		//Embedded(em, emf); //임베디드 설정.
		//ValueTypeAndImmutableObject(em, emf);//값 타입과 불변 객체
		//값 타입 비교는 ValueTpyeCompared_09_.class에서 확인하는게 좋다.
		//ValueTypeCollection(em, emf);     // 값 타입 컬렉션사용.
	}

	private static void ValueTypeCollection(EntityManager em, EntityManagerFactory emf) {
		/** 값 타입 컬렉션은 쓰면 안된다.(중요!!!! 매우 매우 중요!!!!)
		 * 값 타입의 컬렉션의 제약사항
		 *  값 타입은 엔티티와 다르게 식별자 개념이 없다.
		 *  값은 변경하면 추적이 어렵다
		 *  값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다.(중요!)
		 *  값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본키를 구성해야함: null X, 중복 저장 X
		 */
		
		Member member= ValueTypeCollectionAdd(em, emf); //추가
		ValueTypeCollectionSelect(em, emf, member); //검색
		ValueTypeCollectionModify(em, emf, member); //수정하기.
			
		
		/**
		 * 값 타입 컬렉션 대안
		 *  실무에서는 상황에 따라 값 타입 컬렉션 대신에 일대다 관계를 고려
		 *  일대다 관계를 위한 엔티티를 만들고, 여기에서 값 타입을 사용
		 *  영속성 전이+고아 객체 제거를 사용해서 값 타입 컬렉션 처럼 사용!
		 *  EX)AddressEntitiy
		 */	
		
	}

	private static void ValueTypeCollectionModify(EntityManager em, EntityManagerFactory emf, Member member) {
		/*
		 * 값타입의 수정은 갈아껴줘야한다.
		 * 
		 * 수정할 영속성을 찾아서 수정시켜야 한다.
		 */
		
		Member findMember= em.find(Member.class, member.getId());
		Address oldAddress = findMember.getAddress();
		findMember.setAddress(new Address("newCity", oldAddress.getStreet(), oldAddress.getZipcode()));
		
		
		//치킨 -> 한식
		//언제나 생각하자 임베디드 타입은 무조건 값을 삭제해서 다시 집어 넣거나, 아니면 통째로 바꿔줘야한다는것!
		findMember.getFavoriteFoods().remove("치킨");
		findMember.getFavoriteFoods().add("한식");
				
		//매우 중요! 컬렉션에서 객체 타입은 boolean으로 찾는데 boolean은 equals로 찾는다. 그래서 equals를 정의를 해줘야 값을 삭제와 추가를 오류 없이 할 수 있다.
//		findMember.getAddressesHistory().remove(new Address("old1", "street", "10000"));
//		findMember.getAddressesHistory().add(new Address("NewCity1", "street", "10000"));
		
	}

	private static void ValueTypeCollectionSelect(EntityManager em, EntityManagerFactory emf, Member member) {
		em.flush();
		em.clear();
		
		System.out.println("=============START==================");
		member = em.find(Member.class, member.getId());
		
//		List<AddressEntity> addressHistory=member.getAddressesHistory();		
//		for(AddressEntity address: addressHistory) {
//			System.out.println("address = " + address.getAddress().getCity());
//		}
		
		Set<String> favoriteFoods=member.getFavoriteFoods();
		for(String favoriteFood: favoriteFoods)
		{
			System.out.println("favoriteFood = " + favoriteFood);
		}		
	}

	private static Member ValueTypeCollectionAdd(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 값 타입 저장 
		 */
		Member member = new Member();
		 member.setName("member1");
		 member.setAddress(new Address("homeCity", "street", "10000"));
		 
		 member.getFavoriteFoods().add("치킨");
		 member.getFavoriteFoods().add("족발");
		 member.getFavoriteFoods().add("피자");
		 
		 member.getAddressesHistory().add(new AddressEntity("old1", "street", "10000"));
		 member.getAddressesHistory().add(new AddressEntity("old2", "street", "10000"));
		 
		 em.persist(member);
		 
		 return member;	
	}

	private static void ValueTypeAndImmutableObject(EntityManager em, EntityManagerFactory emf) {
		/**
		 * 객체 타입의 한계
		 *  항상 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다.
		 *  문제는 임베디드 타입처럼 직접 정의한 값 타입은 자바의 "기본" 타입이 아니라 "객체" 타입니다.
		 *  자바 기본 타입에 값을 대입하면 값을 복사한다.
		 *  객체 타입은 참조 값을 직접 대입하는 것을 막을 방법이 없다.
		 *  객체의 공유 참조는 피할 수 없다.
		 **/
		Address address = new Address("city", "street", "10000");
		
		Member member1= new Member();
		member1.setName("member1");
		member1.setAddress(address);
		em.persist(member1);
			
		//문제사항 발생 개발자는 member1의 주소를 바꿀려고 하는데 결과가 다르게 나옴(사이드 이펙트 발생) 
		//해결방한. Address의 객체를 새롭게 만든다.
		//이렇게 하는 이유는 값을 변경할때는 통째로 변경 하기 때문이다.
		Address copyAdress  = new Address("NewCity", address.getStreet(), address.getZipcode());
		
		Member member2 =new Member();
		member2.setName("member2");
		member2.setAddress(copyAdress);
		em.persist(member2);
		
	}

	private static void Embedded(EntityManager em, EntityManagerFactory emf) {
		/**
		 * @Embeddable: 값 타입을 "정의"하는 곳에 표시 -> 반드시 "빈" 생성자를 생성해야한다.
		 * @Embedded: 값 타입을 "사용"하는 곳에 표시
		 * 
		 */
		
		/**
		 * 임베디드 타입은 엔티티의 값일 뿐이다.
		 * 임베디드 타입을 사용하기 전화 후에 매핑하는 테이블은 같다.
		 * 객체와 테이블을 아주 세밀하게 매핑하는 것이 가능
		 * 잘 설계한 ORM 애플리케이션은 매핑한 테이블의 수보다 클래스의 수가 더 많음.
		 */
			
		Member member = new Member();
		member.setName("hello");
		member.setAddress(new Address("city", "street", "10000"));
		em.persist(member);
		
	}

	/**
     * 08 프록시와 연관관계 관리 
     **/
	private static void ProxyAssociation_08(EntityManager em, EntityManagerFactory emf) {
		
// ========================================
		//프록시
		
		/**
		 * 새로 하는 이유
		 * member 1, 2는 위에 쓰고있어서  다시 가져오면 객체를 불러오기때문에
		 * 새로 선언해줬다.
		 */ 
//       logic(em);
//       ValueCompare(em);
//       NoManaged(em, emf);
       
// =============================== 
       // 지연로딩 테스트
       //LazyLoding(em, emf);
//       EagerLoding(em, emf);
		 
//===============================
		// 영속성 전이(cascade)와 고아객체		
		//Cascade(em);
		//Orphan(em);
		

		
	}

	private static void Orphan(EntityManager em) {
		// 주의사항
		// 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능.
		// 참조하는 곳이 하나일 때 사용해야함!
		// 특정 엔티티가 개인 소유할 때 사용!
		// @OneToOne, @OneToMany만 가능
		// 참고: 개념적으로 부모를 제거하면 자식은 고아가 된다. 따라서 고아 객체 제거 기능을 활성화 하면, 부모를 제거할 때
		// 자식도 함께 제거된다. 이것은 CascadeType.REMOVE처럼 동작한다.
		
		
		/**
		 * 영속성 전이 + 고아 객체, 생명주기
		 * CascadeType.ALL + orphanRemovel= true
		 * 스스로 생명주기를 관리하는 엔티티는  em.persist()로 영속화, em.remove()로 제거
		 * 두 옵션을 모두 활성화 하면 부모 엔ㅇ티티를 토앻서 자식의 생명 주기를 관리할 수 있다.
		 * 도메인 주도 설계(DDD)의 Aggregate Root개념을 구현할 때 유용
		 */
		
		
		
		Child child1= new Child();
		Child child2 = new Child();
		
		Parent parent = new Parent();
		parent.addChild(child1);
		parent.addChild(child2);
		
		em.persist(parent);
		
		em.flush();
		em.clear();
		
		Parent findParent=em.find(Parent.class, parent.getId());
		findParent.getChildList().remove(0);
		
	}


	private static void Cascade(EntityManager em) {
		
		// 주의사항 ex) child가 현재 Parent에 한곳에서만 관리를 하는데
		// 다른곳(Member)에서도 관리가된다고하면 Cascade를 쓰지 말아야한다.
		// 라이프사이클이 같을때(등록 삭제), 단일소유자일때!. 
		/**
		 *  CASCADE 종류
		 *  
		 *  ALL: 모두 적용
		 *  Persist:영속된것만
		 *  
		 *  ...(여러가지 있는데 잘 안쓰임)
		 */
		Child child1= new Child();
		Child child2 = new Child();
		
		Parent parent = new Parent();
		parent.addChild(child1);
		parent.addChild(child2);
		
		em.persist(parent);
	}


	private static void EagerLoding(EntityManager em, EntityManagerFactory emf) {

//		Member객체에 ManyToOne의 fetch 전략을 EAGER로 setting해줘야 지연로딩 		
//	   @ManyToOne(fetch = FetchType.EAGER)
//	    @JoinColumn(name = "TEAM_ID")
//	    private Team team;
		
		Member member1 = new Member();
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);
		
		member1.setTeam(team);
		member1.setName("member1");
		
		em.persist(member1);
		

		em.flush();
		em.clear();
		
		Member m = em.find(Member.class, member1.getId());
		System.out.println("m = " + m.getTeam().getClass());//Proxy가 생김.
		
		System.out.println("=========================");
		System.out.println("teamName = "+m.getTeam().getName());; //이때  프록시 초기화 
		
		System.out.println("=========================");
		
	}


	private static void LazyLoding(EntityManager em, EntityManagerFactory emf) {
		// 지연로딩은 실제 team을 사용하는 시점에 초기화(중요)

//			Member객체에 ManyToOne의 fetch 전략을 Lazy로 setting해줘야 지연로딩 		
//		   @ManyToOne(fetch = FetchType.LAZY)
//		    @JoinColumn(name = "TEAM_ID")
//		    private Team team;
		
		Member member1 = new Member();
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);
		
		member1.setTeam(team);
		member1.setName("member1");
		
		em.persist(member1);
		
		em.flush();
		em.clear();
		
		Member m = em.find(Member.class, member1.getId());
		System.out.println("m = " + m.getTeam().getClass());//실제 객체가 넘오옴.
		
		System.out.println("=========================");
		m.getTeam().getName(); //이때  프록시 초기화 
		System.out.println("=========================");
		
	}


	private static void NoManaged( EntityManager em, EntityManagerFactory emf) {
		 //================================== 영속성 관리 안할시.
	     
        Member member5 = new Member();
        em.persist(member5);

        em.flush();
        em.clear();
                
        Member refMember= em.getReference(Member.class, member5.getId());
       
        System.out.println("isLoaded= "+ emf.getPersistenceUnitUtil().isLoaded(refMember));
       
        refMember.getName(); //강제 호출로 영속성 추가.
		em.detach(refMember);
		
		System.out.println("isLoaded= "+ emf.getPersistenceUnitUtil().isLoaded(refMember));
		
		//System.out.println("detach된 reference = "+ refMember.getName()); //관리가 안되므로 에러가 나온다.	
	}

	public static void ValueCompare(EntityManager em) {

		/**
		 *  키 포인트! 
		 *  getReference의 반환된 클래스와
		 *  find의 반환된 클래스는 같다!
		 */
			
	       Member member3 = new Member();
	         Member member4 = new Member();
	         em.persist(member3);
	         em.persist(member4);
	         
	         em.flush();
	         em.clear();
	         Member m1_1=em.getReference(Member.class, member3.getId());
	         Member reference = em.find(Member.class, member3.getId());
	                 		
		System.out.println("reference = " + reference.getClass());
		System.out.println("m1_1 = " + m1_1.getClass());
		
		//============================= 테스트 결과내용
		
		/*
		 *  reference와 m1_1은 결국엔 같은 프록시 값이 반환 된다.(같은 class가 반환됨.) 
		 */		
		System.out.println("a == a : "+ (m1_1== reference));
	}

	public static void logic(EntityManager em) {

        Member member1 = new Member();
        member1.setName("member1");
        em.persist(member1);
        
        Member member2 = new Member();
        member2.setName("member2");
        em.persist(member2);
               
        em.flush();
        em.clear();
        //
        //Member findMember = em.find(Member.class, member.getId());
        Member m1= em.find(Member.class, member1.getId());// Member
        Member m2= em.getReference(Member.class, member2.getId());// Proxy
				
		/**
		 * Proxy또는 영속성으로 관리되는 객체를 instanceof로 비교를 해야한다.
		 */		
		System.out.println("m1 == Member : "+ (m1 instanceof Member));
		System.out.println("m2 == Member : "+ (m2 instanceof Member));
		System.out.println("m1 == m2 : "+ (m1 == m2));
				
	}
	
}
