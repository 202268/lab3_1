package lab3_1;

import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
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

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
class TaxDouble implements TaxPolicy{
	@Override
	public Tax calculateTax(ProductType productType, Money net) {
		return new Tax(net, "");
	}
}
public class BookKeeperTestTaxPolicy {
	@Mock
	ClientData clientData;
	@Mock
	ProductData productData;
	TaxPolicy taxPolicy;
	BookKeeper bookKeeper;
	InvoiceRequest invoiceRequest;
	RequestItemDouble requestItem;
	Money money;
	
	@Before
	public void setUp() {
		bookKeeper=new BookKeeper(new InvoiceFactory());
		clientData=mock(ClientData.class);
		productData=mock(ProductData.class);
		invoiceRequest=new InvoiceRequest(clientData);
		money=new Money(new BigDecimal(100), Currency.getInstance(Locale.UK));
		taxPolicy=new TaxDouble();
		
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
		Invoice invoice=bookKeeper.issuance(invoiceRequest, taxPolicy);	
		verify(productData, times(2)).getType();
	}
	@Test
	public void RequestIssuanceWithOneParameterSpecifiedQuantityShouldReturnExpectedValuesInInvoice() {
		requestItem=new RequestItemDouble(productData, 150, money);
		RequestItemDouble requestItem2=new RequestItemDouble(productData, 2, money);
		invoiceRequest.add(requestItem);
		invoiceRequest.add(requestItem2);
		Invoice invoice=bookKeeper.issuance(invoiceRequest, taxPolicy);	
		assertThat(invoice.getItems().get(0).getQuantity(), Matchers.is(150));
	}
	
}
