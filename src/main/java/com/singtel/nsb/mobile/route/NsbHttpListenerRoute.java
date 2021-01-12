package com.singtel.nsb.mobile.route;

import static com.singtel.nsb.mobile.constants.HeaderConstants.EXTERNAL_TRANSACTION_ID;
import static com.singtel.nsb.mobile.constants.HeaderConstants.ID_PATH_PARAM;
import static com.singtel.nsb.mobile.constants.HeaderConstants.NAME;
import static com.singtel.nsb.mobile.constants.Routes.DELETE;
import static com.singtel.nsb.mobile.constants.Routes.GET;
import static com.singtel.nsb.mobile.constants.Routes.PATCH;
import static com.singtel.nsb.mobile.constants.Routes.POST;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.singtel.nsb.framework.constant.IConstants;
import com.singtel.nsb.framework.exception.NsbException;
import com.singtel.nsb.framework.uuid.CorrelatioIdGenerator;
import com.singtel.nsb.mobile.processor.DeleteSubProfileConfigProcessor;
import com.singtel.nsb.mobile.processor.GetSubProfileConfigProcessor;
import com.singtel.nsb.mobile.processor.PatchSubProfileConfigProcessor;
import com.singtel.nsb.mobile.processor.PostSubProfileConfigProcessor;
import com.singtel.nsb.mobile.processor.ProducerProccessor;
import com.singtel.nsb.mobile.util.ErrorUtil;
import com.singtel.nsb.mobile.validator.DeleteRequestValidator;
import com.singtel.nsb.mobile.validator.PatchRequestValidator;
import com.singtel.nsb.mobile.validator.PostRequestValidator;

@Component
public class NsbHttpListenerRoute extends RouteBuilder {

	@Autowired
	Environment env;

	@Autowired
	private GetSubProfileConfigProcessor getSubProfileConfigProcessor;
	@Autowired
	private PatchSubProfileConfigProcessor patchSubProfileConfigProcessor;
	@Autowired
	private DeleteSubProfileConfigProcessor deleteSubProfileConfigProcessor;
	@Autowired
	private PostSubProfileConfigProcessor postSubProfileConfigProcessor;
	@Autowired
	private ProducerProccessor producerProccessor;
	
	@Autowired
	private PatchRequestValidator patchRequestValidator;
	@Autowired
	private PostRequestValidator postRequestValidator;
	@Autowired
	private DeleteRequestValidator deleteRequestValidator;

	private static final String CAMELLOG = "NSB_EDA_SA";

	@Value("${eda.service}")
	private String service;

	@Override
	public void configure() throws Exception {
		
		onException(NsbException.class).handled(true).process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				String error = ErrorUtil.constructError(exchange);
				exchange.getIn().setBody(error);
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_BAD_REQUEST);
			}
		}).end();
		
		rest(service).description("Service Activation & Configuration Services") 
				.get(ID_PATH_PARAM)
				.param().name(NAME).type(RestParamType.query).required(true).endParam()
				.param().name(EXTERNAL_TRANSACTION_ID).type(RestParamType.query).required(true).endParam()
				.description("GET Subscriber Profile")
				.route()
				.setHeader(IConstants.INTERNAL_CORRELATION_ID, method(CorrelatioIdGenerator.class))
				.to(GET.getRoute());
		
		rest(service).patch(ID_PATH_PARAM)
				.description("PATCH Subscriber Profile")
				.type(String.class)
				.route()
				.setHeader(IConstants.INTERNAL_CORRELATION_ID, method(CorrelatioIdGenerator.class))
				.process(patchRequestValidator)
				.to(PATCH.getRoute());
		
		rest(service).delete(ID_PATH_PARAM)
				.description("DELETE Subscriber Profile")
				.type(String.class)
				.route()
				.setHeader(IConstants.INTERNAL_CORRELATION_ID, method(CorrelatioIdGenerator.class))
				.process(deleteRequestValidator)
				.to(DELETE.getRoute());
		
		rest(service).post()
				.description("POST Subscriber Profile")
				.type(String.class)
				.route()
				.setHeader(IConstants.INTERNAL_CORRELATION_ID, method(CorrelatioIdGenerator.class))
				.process(postRequestValidator)
				.to(POST.getRoute());

		from(GET.getRoute()).routeId(GET.getRouteId())
				.log(LoggingLevel.INFO, CAMELLOG, "GET Service invoked")
				.process(getSubProfileConfigProcessor)
				.process(producerProccessor).id("getProducer")
				.log(LoggingLevel.INFO, CAMELLOG, "Get Api processing is Done.")
				.end();
		
		from(PATCH.getRoute()).routeId(PATCH.getRouteId())
				.log(LoggingLevel.INFO, CAMELLOG, "PATCH Service invoked")
				.process(patchSubProfileConfigProcessor)
				.process(producerProccessor).id("patchProducer")
				.log(LoggingLevel.INFO, CAMELLOG, "Patch Api processing is Done.")
				.end();
		
		from(DELETE.getRoute()).routeId(DELETE.getRouteId())
				.log(LoggingLevel.INFO, CAMELLOG, "DELETE Service invoked")
				.process(deleteSubProfileConfigProcessor)
				.process(producerProccessor)
				.log(LoggingLevel.INFO, CAMELLOG, "Delete Api processing is Done.")
				.end();
		
		from(POST.getRoute()).routeId(POST.getRouteId())
				.log(LoggingLevel.INFO, CAMELLOG, "POST Service invoked")
				.process(postSubProfileConfigProcessor)
				.process(producerProccessor)
				.log(LoggingLevel.INFO, CAMELLOG, "Post Api processing is Done.")
				.end();
	}

}
