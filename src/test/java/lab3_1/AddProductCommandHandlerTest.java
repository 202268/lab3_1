package lab3_1;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;

public class AddProductCommandHandlerTest {
	private AddProductCommandHandler addProductCommandHandler;
	private ReservationRepository reservationRepository;
	private ProductRepository productRepository;
	private ClientRepository clientRepository;
	private Reservation reservation;
	private Product product;
	private Client client;
	private Field field;

	@Before
	public void setUp() {
		reservationRepository = mock(ReservationRepository.class);
		productRepository = mock(ProductRepository.class);
		mock(SuggestionService.class);
		clientRepository = mock(ClientRepository.class);
		reservation = mock(Reservation.class);
		product = mock(Product.class);
		client = mock(Client.class);
		addProductCommandHandler = new AddProductCommandHandler();
		accessToPrivateMembers();
		when(reservationRepository.load(anyId())).thenReturn(reservation);
		when(productRepository.load(anyId())).thenReturn(product);
		when(clientRepository.load(anyId())).thenReturn(client);
	}

	@Test
	public void MethodWithTwoCommandShouldCallSaveMethodOneTime() {
		int quantity = 1;
		AddProductCommand addProductCommand = new AddProductCommand(anyId(), anyId(), quantity);
		when(product.isAvailable()).thenReturn(true);
		addProductCommandHandler.handle(addProductCommand);
		verify(reservationRepository, times(1)).save(reservation);
	}

	@Test
	public void MethodWithTwoCommandShouldCallSaveMethodTwoTimes() {
		int quantity = 1;
		AddProductCommand addProductCommand = new AddProductCommand(anyId(), anyId(), quantity);
		AddProductCommand addProductCommand2 = new AddProductCommand(anyId(), anyId(), quantity);
		when(product.isAvailable()).thenReturn(true);
		addProductCommandHandler.handle(addProductCommand);
		addProductCommandHandler.handle(addProductCommand2);
		verify(reservationRepository, times(2)).save(reservation);
	}

	private Id anyId() {
		return new Id("1");
	}

	private void accessToPrivateMembers() {
		try {
			field = AddProductCommandHandler.class.getDeclaredField("reservationRepository");
			field.setAccessible(true);
			field.set(addProductCommandHandler, reservationRepository);
			field = AddProductCommandHandler.class.getDeclaredField("productRepository");
			field.setAccessible(true);
			field.set(addProductCommandHandler, productRepository);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
