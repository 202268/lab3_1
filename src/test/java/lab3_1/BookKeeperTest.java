package lab3_1;
import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.invoicing.BookKeeper;
import pl.com.bottega.ecommerce.sales.domain.invoicing.Invoice;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceFactory;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceRequest;
import pl.com.bottega.ecommerce.sales.domain.invoicing.RequestItemDouble;
import pl.com.bottega.ecommerce.sales.domain.invoicing.Tax;
import pl.com.bottega.ecommerce.sales.domain.invoicing.TaxPolicy;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

public class BookKeeperTest {
	ClientData clientData;
	ProductData productData;
	TaxPolicy taxPolicy;
	BookKeeper bookKeeper;
	InvoiceRequest invoiceRequest;
	RequestItemDouble requestItem;
	Money money;
	
	@Before
	public void setUp() {
		bookKeeper=new BookKeeper(new InvoiceFactory());
		clientData=new Client().generateSnapshot();
		productData=mock(ProductData.class);
		taxPolicy=mock(TaxPolicy.class);
		invoiceRequest=new InvoiceRequest(clientData);
		money=new Money(new BigDecimal(100), Currency.getInstance(Locale.UK));
		when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(money, ""));
		
	}
	@Test
	public void RequestIssuanceWithOneParameterShouldReturnOneInvoice() {
		requestItem=new RequestItemDouble(productData, 1, money);
		invoiceRequest.add(requestItem);
		Invoice invoice=bookKeeper.issuance(invoiceRequest, taxPolicy);
		assertThat(invoice.getItems().size(), Matchers.is(1));
	}
	@Test
	public void RequestIssuanceWithTwoParameterShouldCallMethodTwoTimes() {
		requestItem=new RequestItemDouble(productData, 1, money);
		RequestItemDouble requestItem2=new RequestItemDouble(productData, 2, money);
		invoiceRequest.add(requestItem);
		invoiceRequest.add(requestItem2);
		bookKeeper.issuance(invoiceRequest, taxPolicy);	
		verify(taxPolicy, times(2)).calculateTax(any(ProductType.class), any(Money.class));
	}
	@Test
	public void RequestIssuanceWithOneParameterSpecifiedQuantityShouldReturnExpectedValuesInInvoice() {
		requestItem=new RequestItemDouble(productData, 150, money);
		invoiceRequest.add(requestItem);
		Invoice invoice=bookKeeper.issuance(invoiceRequest, taxPolicy);	
		assertThat(invoice.getItems().get(0).getQuantity(), Matchers.is(150));
	}
	@Test
	public void RequestIssuanceWithoutParametersShouldntCallAnyMethod() {
		bookKeeper.issuance(invoiceRequest, taxPolicy);	
		verify(taxPolicy, times(0)).calculateTax(any(ProductType.class), any(Money.class));
	}
}
