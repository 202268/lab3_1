package lab3_1;

import static org.mockito.Matchers.any;
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
	AddProductCommandHandler addProductCommandHandler;
	private ReservationRepository reservationRepository;
	ProductRepository productRepository;
	SuggestionService suggestionService;
	ClientRepository clientRepository;
	SystemContext systemContext;
	Reservation reservation;
	Product product;
	Client client;
	Field field;

	@Before
	public void setUp() {
		reservationRepository = mock(ReservationRepository.class);
		productRepository = mock(ProductRepository.class);
		suggestionService = mock(SuggestionService.class);
		clientRepository = mock(ClientRepository.class);
		reservation = mock(Reservation.class);
		product = mock(Product.class);
		client = mock(Client.class);
		addProductCommandHandler = new AddProductCommandHandler();

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
		when(reservationRepository.load(any(Id.class))).thenReturn(reservation);
		when(productRepository.load(any(Id.class))).thenReturn(product);
		when(clientRepository.load(any(Id.class))).thenReturn(client);
	}

	@Test
	public void MethodWithTwoCommandShouldCallSaveMethodOneTime() {
		AddProductCommand addProductCommand = new AddProductCommand(new Id("1"), new Id("1"), 1);
		when(product.isAvailable()).thenReturn(true);
		addProductCommandHandler.handle(addProductCommand);
		verify(reservationRepository, times(1)).save(reservation);
	}

	@Test
	public void MethodWithTwoCommandShouldCallSaveMethodTwoTimes() {
		AddProductCommand addProductCommand = new AddProductCommand(new Id("1"), new Id("1"), 1);
		AddProductCommand addProductCommand2 = new AddProductCommand(new Id("1"), new Id("1"), 1);
		when(product.isAvailable()).thenReturn(true);
		addProductCommandHandler.handle(addProductCommand);
		addProductCommandHandler.handle(addProductCommand2);
		verify(reservationRepository, times(2)).save(reservation);
	}

}
