package jpabook.jpashop;

import jpabook.jpashop.domain.Address;

public class ValueTpyeCompared_09 {

	public static void main(String[] args) {
		// equals()를 사용하면 자동으로 만들어주는 equals를 써야 오류가 안난다.(덤으로 hashCode도 만들어줌!)
		
		int a =10;
		int b= 10;
		System.out.println("a == b : " + (a==b));
		
		Address address1 = new Address("city", "street", "10000");
		Address address2 = new Address("city", "street", "10000");
		System.out.println("address1 == address2 : " +(address1 == address2)); //당연히 false
		System.out.println("addess1 equals address2 : " + (address1.equals(address2)));

	}

}
