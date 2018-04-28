package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductDataBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class BookKeeperTest {
	ClientData clientData;
	ProductData productData;
	TaxPolicy taxPolicy;
	BookKeeper bookKeeper;
	InvoiceRequest invoiceRequest;
	RequestItem requestItem;
	Money money;

	@Before
	public void setUp() {
		bookKeeper = new BookKeeper(new InvoiceFactory());
		clientData = new Client().generateSnapshot();
		productData = new ProductDataBuilder().productData().build();
		taxPolicy = mock(TaxPolicy.class);
		invoiceRequest = new InvoiceRequest(clientData);
		money = new Money(new BigDecimal(100), Currency.getInstance(Locale.UK));
		when(taxPolicy.calculateTax(productData.getType(), money)).thenReturn(new Tax(money, ""));

	}

	@Test
	public void RequestIssuanceWithOneParameterShouldReturnOneInvoice() {
		requestItem = new RequestItemBuilder().requestItem().withProductData(productData).withTotalCost(money).build();
		invoiceRequest.add(requestItem);
		Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
		assertThat(invoice.getItems().size(), Matchers.is(1));
	}

	@Test
	public void RequestIssuanceWithTwoParameterShouldCallMethodTwoTimes() {
		requestItem = new RequestItemBuilder().requestItem().withProductData(productData).withTotalCost(money).build();
		RequestItem requestItem2 = new RequestItemBuilder().requestItem().withProductData(productData)
				.withTotalCost(money).build();
		invoiceRequest.add(requestItem);
		invoiceRequest.add(requestItem2);
		bookKeeper.issuance(invoiceRequest, taxPolicy);
		verify(taxPolicy, times(2)).calculateTax(productData.getType(), money);
	}

	@Test
	public void RequestIssuanceWithOneParameterSpecifiedQuantityShouldReturnExpectedValuesInInvoice() {
		requestItem = new RequestItemBuilder().withProductData(productData).withTotalCost(money).build();
		invoiceRequest.add(requestItem);
		Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
		assertThat(invoice.getItems().get(0).getQuantity(), Matchers.is(1));
	}

	@Test
	public void RequestIssuanceWithoutParametersShouldntCallAnyMethod() {
		bookKeeper.issuance(invoiceRequest, taxPolicy);
		verify(taxPolicy, times(0)).calculateTax(productData.getType(), money);
	}
}
