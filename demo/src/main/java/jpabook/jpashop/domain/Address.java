package jpabook.jpashop.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

	@Column(length = 10)
    private String city;
	@Column(length = 20)
    private String street;
	@Column(length = 5)
    private String zipcode;
    
    
    
	protected Address() {	
	}
	
	public Address(String city, String street, String zipcode) {
		
		this.city = city;
		this.street = street;
		this.zipcode = zipcode;
	}

	public String fullAddress() {
		return getCity() + " " + getStreet() + " " +  getZipcode();
	}

	public String getCity() {
		return city;
	}
	
	public String getStreet() {
		return street;
	}
	
	public String getZipcode() {
		return zipcode;
	}

	/**
	 * @param city the city to set
	 */
	private void setCity(String city) {
		this.city = city;
	}

	/**
	 * @param street the street to set
	 */
	private void setStreet(String street) {
		this.street = street;
	}

	/**
	 * @param zipcode the zipcode to set
	 */
	private void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	
	
	//웬만하면 getter로 써야한다. 
	@Override
	public int hashCode() {
		return Objects.hash(getCity(), getStreet(), getZipcode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		return Objects.equals(getCity(), other.city) && Objects.equals(getStreet(), other.street)
				&& Objects.equals(getZipcode(), other.zipcode);
	}




	
	

   
	
	
	
}
