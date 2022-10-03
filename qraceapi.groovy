//@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1' )
//@Grab(group='org.apache.httpcomponents', module='httpclient', version='4.3.5' )
//@Grab(group='org.apache.httpcomponents', module='httpmime', version='4.3.5' )


import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.GET
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.ByteArrayBody
import org.apache.http.entity.mime.content.StringBody
import groovy.json.JsonSlurper
import groovy.json.*

String url = "http://10.0.0.93:8083"
String sPipelinename = "APIDummy""
def http = new groovyx.net.http.HTTPBuilder(url)
http.request(POST){multipartRequest ->
		uri.path ='/api/executePipeline'
		uri.query =[applicationName:'OpenBank',pipelineName:sPipelinename]
headers.Authorization= "Basic ${"admin:rta@123".bytes.encodeBase64().toString()}"
headers.Accept= 'application/json'
headers.contentType = "ContentType: application/json"
print("Basic ${"admin:rta@123".bytes.encodeBase64().toString()}")
	   response.success = { resp,data ->
             println "Success!"
             //println data
             def str= data
             println str
            //Get
            //String runStatus="${str}"
            def runStatus=""
            while(runStatus!="FINISHED"){
                 String runId="${str}"
                 //print("runid"+"${str}")
                 if(runId != "")
                 {
                     try{  
                        def http1 = new HTTPBuilder(url)
		    http1.get( path : '/api/getCTpipelineStatus', query : [executionId:"${runId}"] )
	              { resp1,data1 ->
		        def str1= data1
                             //println data1
                             def respVal= new JsonBuilder(str1).toPrettyString()
                             def statVal = new groovy.json.JsonSlurperClassic().parseText(respVal)
                             runStatus = "${statVal.status}"
                             sleep(5000)
                             println("Status of the Run : "+"${statVal.status}")
                            
                         }//resp1
                         
                         if(runStatus == "FINISHED" || runStatus=="FAILED" || runStatus=="PASSED" ){
                                println("Status of the Run : "+"${runStatus}")
                                break
                               }//if break    
                             
                      }//try
                      catch(groovyx.net.http.HttpResponseException e){
                             if(runStatus=="FINISHED"){ 
                                 println e.toString()
                              }
                      }//catch
                  } //if        
            }//while
    }
}//http1          
            
    