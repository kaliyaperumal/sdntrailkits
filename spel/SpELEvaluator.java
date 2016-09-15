import org.springframework.expression.Expression;  
import org.springframework.expression.ExpressionParser;  
import org.springframework.expression.spel.standard.SpelExpressionParser;  
import org.springframework.expression.spel.support.StandardEvaluationContext;  
import org.springframework.expression.EvaluationContext;    
public class SpELEvaluator {  
public static void main(String[] args) {  


      NetworkMetric metric = new NetworkMetric(20,25,35);
      EvaluationContext context = new StandardEvaluationContext(metric);

      ExpressionParser parser = new SpelExpressionParser();  

  
      Expression exp = parser.parseExpression("portutilization <= 35 and packetdrops == 25");  
      boolean result = exp.getValue(context, Boolean.class);
      System.out.println("Evaluated to:" + result);  
   }
}
