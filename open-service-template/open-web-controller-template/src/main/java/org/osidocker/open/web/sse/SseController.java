package org.osidocker.open.web.sse;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SseController {
	
	@RequestMapping(value="/openAPI/push",produces="text/event-stream")
	public @ResponseBody String push(){
		Random r = new Random();
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "data:Testing 1,2,3"+r.nextInt()+"\n\n";
	}
	
	@RequestMapping("/openAPI/sse")
	public String sseIndex(){
		return "sseTemplate";
	}
}
