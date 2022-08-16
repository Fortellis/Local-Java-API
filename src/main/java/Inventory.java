import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.okta.jwt.*;
import java.time.Duration;

public class Inventory extends HttpServlet{

    public Inventory(){
        super();
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
        String authorizationHeader = request.getHeader("Authorization");
        try{
            AccessTokenVerifier jwtVerifier = JwtVerifiers.accessTokenVerifierBuilder()
                .setIssuer("https://identity.fortellis.io/oauth2/aus1p1ixy7YL8cMq02p7")
                .setAudience("api_providers")
                .setConnectionTimeout(Duration.ofSeconds(1))
                .build();
            Jwt jwt = jwtVerifier.decode(authorizationHeader.replace("Bearer", ""));
            
            if(jwt.getClaims().get("sub").equals("{yourAPIKey}")){
                System.out.println("The strings are equal.");
            }else{
                throw new ServletException("You must have the same subject in your token");
            }

            if(!request.getHeader("Subscription-Id").equals("{yourSubscribptionId}")){
                System.out.println("You have not subscribed");
                throw new ServletException("You must have the correct Subscription-Id to get a response");
            }

            try{

                ClassLoader classLoader = getClass().getClassLoader();
                File  wholeFile =new File (classLoader.getResource("inventory.json").getFile());
                FileInputStream fis = new FileInputStream(wholeFile);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String concatenatedFile = "";
                String strLine;
                //Put all the lines from the file back together to make the original object.
                while((strLine = br.readLine()) != null){
                    //System.out.println(strLine);
                    concatenatedFile = concatenatedFile + strLine;
                }
                System.out.println("This is the concatenatedFile: " + concatenatedFile);
                PrintWriter healthCheckResponse = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                healthCheckResponse.print(concatenatedFile);
                healthCheckResponse.flush();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        
        }catch(Exception e){
            System.out.println("You had a problem with the token.");
        }
    }
}