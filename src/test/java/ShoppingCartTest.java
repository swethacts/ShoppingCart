import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.eShoppingCart.model.BillingAddress;
import com.eShoppingCart.model.Cart;
import com.eShoppingCart.model.CartItem;
import com.eShoppingCart.model.Customer;
import com.eShoppingCart.model.CustomerOrder;
import com.eShoppingCart.model.Product;
import com.eShoppingCart.model.ShippingAddress;
import com.eShoppingCart.service.CartItemService;
import com.eShoppingCart.service.CartService;
import com.eShoppingCart.service.CustomerOrderService;
import com.eShoppingCart.service.CustomerService;
import com.eShoppingCart.service.ProductService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
(
  {
   "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml"
  }
)
@WebAppConfiguration
public class ShoppingCartTest {

	@Autowired
	ProductService productService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private CustomerOrderService customerOrderService;
	
	
	@Test
	public void testProoductsSize() {
		List<Product> products = productService.getProductList();
		assertNotNull(products);
		assertTrue(products.size() > 0);
	}

	@Test
	public void testProductByID() {
		Product prod = productService.getProductById(33);
		assertEquals("Guitar", prod.getName()); 
	}
	
	@Test
	public void testCustomersPresent() {
		List<Customer> customers = customerService.getAllCustomers();
		assertNotNull(customers);
		assertTrue(customers.size() > 0);
	}
	
	@Test
	public void testAddCustomer() {
		
		Date today=new Date();
		String custName = "UBSUser"+today.getHours()+today.getMinutes();
		Customer cust = new Customer();
		cust.setUsername(custName);
		cust.setPassword("ubsuser");
		cust.setCustomerName(custName);
		cust.setCustomerEmail(custName+"@ubs.com");
		
		BillingAddress billAdd = new BillingAddress();
		billAdd.setStreetName("Taylor Street");
		billAdd.setApartmentNumber("4050");
		billAdd.setCity("San Diego");
		billAdd.setState("CA");
		billAdd.setCountry("United States");
		billAdd.setZipCode("CA 92110");
		cust.setBillingAddress(billAdd);
		
		ShippingAddress shipAdd = new ShippingAddress();
		shipAdd.setStreetName("Taylor Street");
		shipAdd.setApartmentNumber("4050");
		shipAdd.setCity("San Diego");
		shipAdd.setState("CA");
		shipAdd.setCountry("United States");
		shipAdd.setZipCode("CA 92110");
		
		cust.setShippingAddress(shipAdd);
		
		customerService.addCustomer(cust);
		
		assertNotNull(customerService.getCustomerByUsername(custName));
	}
	
	@Test
	public void testAddAdminProd() {
		Date today=new Date();
		String prodName = "Speaker-Version"+today.getHours()+today.getMinutes();
		
		Product product = new Product();
		
		product.setName(prodName);
		product.setCategory("record");
		product.setDescription("Sample for record");
		product.setPrice(20000);
		product.setCondition("new");
		product.setStatus("active");
		product.setUnits(15);
		product.setManufacturer("ABZ");
		productService.addProduct(product);
		
		assertNotNull(productService.getProductByName(prodName));
	}
	
	@Test
	public void testCreateOrder() {
		double grandTotal = 0;

		Customer customer = customerService.getCustomerById(6);
		Cart cart = customer.getCart();
		List<CartItem> cartItems = new ArrayList<CartItem>();

		CartItem cartitem1 = new CartItem();
		Product prod = productService.getProductById(32);
		cartitem1.setProduct(prod);
		cartitem1.setQuantity(2);
		double price = prod.getPrice() * cartitem1.getQuantity();
		cartitem1.setTotalPrice(price);
		 cartitem1.setCart(cart);

		CartItem cartitem2 = new CartItem();
		Product prod2 = productService.getProductById(33);
		cartitem2.setProduct(prod2);
		cartitem2.setQuantity(2);
		double price2 = prod.getPrice() * cartitem2.getQuantity();
		cartitem2.setTotalPrice(price2);
		 cartitem2.setCart(cart);

		cartItems.add(cartitem1);
		cartItems.add(cartitem2);

		cart.setCartItems(cartItems);

		grandTotal = cartitem1.getTotalPrice() + cartitem2.getTotalPrice();

		cart.setGrandTotal(grandTotal);

		cartItemService.addCartItem(cartitem1);
		cartItemService.addCartItem(cartitem2); 
		
		CustomerOrder order = new CustomerOrder();
		order.setCart(cart);

		order.setCustomer(customer);
		order.setBillingAddress(customer.getBillingAddress());
		order.setShippingAddress(customer.getShippingAddress());

		customerOrderService.addCustomerOrder(order);
		
		assertNotNull(customerOrderService.getCustomerOrderGrandTotal(cart.getCartId()));

	}
	
}
