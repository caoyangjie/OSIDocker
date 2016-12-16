package org.osidocker.open.web.converter;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

public class DemoMessageConverter extends AbstractHttpMessageConverter<DemoConverterVO> {
	
	public DemoMessageConverter(){
		super(new MediaType("application","x-wisely",Charset.forName("UTF-8")));
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return DemoConverterVO.class.isAssignableFrom(clazz);
	}

	@Override
	protected DemoConverterVO readInternal(Class<? extends DemoConverterVO> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		String temp = StreamUtils.copyToString(inputMessage.getBody(), Charset.forName("UTF-8"));
		return new DemoConverterVO(temp);
	}

	@Override
	protected void writeInternal(DemoConverterVO t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		outputMessage.getBody().write(t.toString().getBytes());
	}

}
