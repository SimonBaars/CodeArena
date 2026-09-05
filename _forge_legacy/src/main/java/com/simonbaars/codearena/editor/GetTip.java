package com.simonbaars.codearena.editor;

import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.codearena.model.MetricProblem;

public interface GetTip {
	public default String getTip(MetricProblem problem) {
		if(problem.getType() == ProblemType.UNITINTERFACESIZE) {
			return getUnitInterfaceSizeTip();
		} else if(problem.getType() == ProblemType.DUPLICATION) {
			return getDuplicationTip();
		} else if(problem.getType() == ProblemType.UNITVOLUME || problem.getType() == ProblemType.UNITCOMPLEXITY) {
			return getLongMethodTip();
		}
		return "";
	}

	public default String getUnitInterfaceSizeTip() {
		return "<article>\n" + 
				"    <h1 class=\"title\">Long Parameter List</h1>\n" + 
				"            \n" + 
				"    <h3>Signs and Symptoms</h3>\n" + 
				"<p>More than three or four parameters for a method.</p>\n" + 
				"\n" + 
				"<h3>Reasons for the Problem</h3>\n" + 
				"<p>A long list of parameters might happen after several types of algorithms are merged in a single method. A long list may have been created to control which algorithm will be run and how.</p>\n" + 
				"<p>Long parameter lists may also be the byproduct of efforts to make classes more independent of each other. For example, the code for creating specific objects needed in a method was moved from the method to the code for calling the method, but the created objects are passed to the method as parameters. Thus the original class no longer knows about the relationships between objects, and dependency has decreased. But if several of these objects are created, each of them will require its own parameter, which means a longer parameter list.</p>\n" + 
				"<p>It’s hard to understand such lists, which become contradictory and hard to use as they grow longer. Instead of a long list of parameters, a method can use the data of its own object. If the current object doesn’t contain all necessary data, another object (which will get the necessary data) can be passed as a method parameter.</p>\n" + 
				"<h3>Treatment</h3>\n" + 
				"<ul>\n" + 
				"<li>\n" + 
				"<p>Check what values are passed to parameters. If some of the arguments are just results of method calls of another object, use <a href=\"https://refactoring.guru/replace-parameter-with-method-call\">Replace Parameter with Method Call</a>. This object can be placed in the field of its own class or passed as a method parameter.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>Instead of passing a group of data received from another object as parameters, pass the object itself to the method, by using <a href=\"https://refactoring.guru/preserve-whole-object\">Preserve Whole Object</a>.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>If there are several unrelated data elements, sometimes you can merge them into a single parameter object via <a href=\"https://refactoring.guru/introduce-parameter-object\">Introduce Parameter Object</a>.</p>\n" + 
				"</li>\n" + 
				"</ul>\n" + 
				"\n" + 
				"<h3>Payoff</h3>\n" + 
				"<ul>\n" + 
				"<li>\n" + 
				"<p>More readable, shorter code.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>Refactoring may reveal previously unnoticed duplicate code.</p>\n" + 
				"</li>\n" + 
				"</ul>\n" + 
				"<h3>When to Ignore</h3>\n" + 
				"<ul>\n" + 
				"<li>Don’t get rid of parameters if doing so would cause unwanted dependency between classes.</li>\n" + 
				"</ul>\n" + 
				"\n" + 
				"<p>Read more at: <a href=\"https://refactoring.guru/smells/long-parameter-list\">https://refactoring.guru/smells/long-parameter-list</a></p>\n" + 
				"\n" + 
				"</article>";
	}
	
	public default String getDuplicationTip() {
		return "<article>\n" + 
				"    \n" + 
				"    <h1 class=\"title\">Duplicate Code</h1>\n" + 
				"    \n" + 
				"\n" + 
				"            \n" + 
				"    <h3>Signs and Symptoms</h3>\n" + 
				"<p>Two code fragments look almost identical.</p>\n" + 
				"\n" + 
				"<h3>Reasons for the Problem</h3>\n" + 
				"<p>Duplication usually occurs when multiple programmers are working on different parts of the same program at the same time. Since they’re working on different tasks, they may be unaware their colleague has already written similar code that could be repurposed for their own needs.</p>\n" + 
				"<p>There’s also more subtle duplication, when specific parts of code look different but actually perform the same job. This kind of duplication can be hard to find and fix.</p>\n" + 
				"<p>Sometimes duplication is purposeful. When rushing to meet deadlines and the existing code is “almost right” for the job, novice programmers may not be able to resist the temptation of copying and pasting the relevant code. And in some cases, the programmer is simply too lazy to de-clutter.</p>\n" + 
				"<h3>Treatment</h3>\n" + 
				"<ul>\n" + 
				"<li>\n" + 
				"<p>If the same code is found in two or more methods in the same class: use <a href=\"https://refactoring.guru/extract-method\">Extract Method</a> and place calls for the new method in both places.</p>\n" + 
				" </li>\n" + 
				"<li>\n" + 
				"<p>If the same code is found in two subclasses of the same level:</p>\n" + 
				"<ul>\n" + 
				"<li>\n" + 
				"<p>Use <a href=\"https://refactoring.guru/extract-method\">Extract Method</a> for both classes, followed by <a href=\"https://refactoring.guru/pull-up-field\">Pull Up Field</a> for the fields used in the method that you’re pulling up.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>If the duplicate code is inside a constructor, use <a href=\"https://refactoring.guru/pull-up-constructor-body\">Pull Up Constructor Body</a>.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>If the duplicate code is similar but not completely identical, use <a href=\"https://refactoring.guru/form-template-method\">Form Template Method</a>.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>If two methods do the same thing but use different algorithms, select the best algorithm and apply <a href=\"https://refactoring.guru/substitute-algorithm\">Substitute Algorithm</a>.</p>\n" + 
				"</li>\n" + 
				"</ul>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>If duplicate code is found in two different classes:</p>\n" + 
				"<ul>\n" + 
				"<li>\n" + 
				"<p>If the classes aren’t part of a hierarchy, use <a href=\"https://refactoring.guru/extract-superclass\">Extract Superclass</a> in order to create a single superclass for these classes that maintains all the previous functionality.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>If it’s difficult or impossible to create a superclass, use <a href=\"https://refactoring.guru/extract-class\">Extract Class</a> in one class and use the new component in the other.</p>\n" + 
				"</li>\n" + 
				"</ul>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>If a large number of conditional expressions are present and perform the same code (differing only in their conditions), merge these operators into a single condition using <a href=\"https://refactoring.guru/consolidate-conditional-expression\">Consolidate Conditional Expression</a> and use <a href=\"https://refactoring.guru/extract-method\">Extract Method</a> to place the condition in a separate method with an easy-to-understand name.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>If the same code is performed in all branches of a conditional expression: place the identical code outside of the condition tree by using <a href=\"https://refactoring.guru/consolidate-duplicate-conditional-fragments\">Consolidate Duplicate Conditional Fragments</a>.</p>\n" + 
				"</li>\n" + 
				"</ul>\n" + 
				"<h3>Payoff</h3>\n" + 
				"<ul>\n" + 
				"<li>\n" + 
				"<p>Merging duplicate code simplifies the structure of your code and makes it shorter.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>Simplification + shortness = code that’s easier to simplify and cheaper to support.</p>\n" + 
				"</li>\n" + 
				"</ul>\n" + 
				"\n" + 
				"<h3>When to Ignore</h3>\n" + 
				"<ul>\n" + 
				"<li>In very rare cases, merging two identical fragments of code can make the code less intuitive and obvious.</li>\n" + 
				"</ul>\n" + 
				"\n" + 
				"    <p>Read more at: <a href=\"https://refactoring.guru/smells/duplicate-code\">https://refactoring.guru/smells/duplicate-code</a></p>\n" + 
				"    \n" + 
				"\n" + 
				"</article>";
	}
	
	public default String getLongMethodTip() {
		return "<article>\n" + 
				"    \n" + 
				"    <h1 class=\"title\">Long Method</h1>\n" + 
				"\n" + 
				"            \n" + 
				"    <h3>Signs and Symptoms</h3>\n" + 
				"<p>A method contains too many lines of code. Generally, any method longer than ten lines should make you start asking questions.</p>\n" + 
				"\n" + 
				"<h3>Reasons for the Problem</h3>\n" + 
				"<p>Like the Hotel California, something is always being added to a method but nothing is ever taken out. Since it’s easier to write code than to read it, this “smell” remains unnoticed until the method turns into an ugly, oversized beast.</p>\n" + 
				"<p>Mentally, it’s often harder to create a new method than to add to an existing one: “But it’s just two lines, there’s no use in creating a whole method just for that...” Which means that another line is added and then yet another, giving birth to a tangle of spaghetti code.</p>\n" + 
				"<h3>Treatment</h3>\n" + 
				"<p>As a rule of thumb, if you feel the need to comment on something inside a method, you should take this code and put it in a new method. Even a single line can and should be split off into a separate method, if it requires explanations. And if the method has a descriptive name, nobody will need to look at the code to see what it does.</p>\n" + 
				"\n" + 
				"<ul>\n" + 
				"<li>\n" + 
				"<p>To reduce the length of a method body, use <a href=\"https://refactoring.guru/extract-method\">Extract Method</a>.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>If local variables and parameters interfere with extracting a method, use <a href=\"https://refactoring.guru/replace-temp-with-query\">Replace Temp with Query</a>, <a href=\"https://refactoring.guru/introduce-parameter-object\">Introduce Parameter Object</a> or <a href=\"https://refactoring.guru/preserve-whole-object\">Preserve Whole Object</a>.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>If none of the previous recipes help, try moving the entire method to a separate object via <a href=\"https://refactoring.guru/replace-method-with-method-object\">Replace Method with Method Object</a>.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>Conditional operators and loops are a good clue that code can be moved to a separate method. For conditionals, use <a href=\"https://refactoring.guru/decompose-conditional\">Decompose Conditional</a>. If loops are in the way, try <a href=\"https://refactoring.guru/extract-method\">Extract Method</a>.</p>\n" + 
				"</li>\n" + 
				"</ul>\n" + 
				"<h3>Payoff</h3>\n" + 
				"<ul>\n" + 
				"<li>\n" + 
				"<p>Among all types of object-oriented code, classes with short methods live longest. The longer a method or function is, the harder it becomes to understand and maintain it.</p>\n" + 
				"</li>\n" + 
				"<li>\n" + 
				"<p>In addition, long methods offer the perfect hiding place for unwanted duplicate code.</p>\n" + 
				"</li>\n" + 
				"</ul>\n" + 
				"\n" + 
				"<h3>Performance</h3>\n" + 
				"<p>Does an increase in the number of methods hurt performance, as many people claim? In almost all cases the impact is so negligible that it’s not even worth worrying about.</p>\n" + 
				"<p>Plus, now that you have clear and understandable code, you’re more likely to find truly effective methods for restructuring code and getting real performance gains if the need ever arises.</p>\n" + 
				"\n" + 
				"    <p>Read more at: <a href=\"https://refactoring.guru/smells/long-method\">https://refactoring.guru/smells/long-method</a></p>\n" + 
				"    \n" + 
				"\n" + 
				"</article>";
	}
}
