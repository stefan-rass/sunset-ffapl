package sunset.gui.logic;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import sunset.gui.interfaces.ISearchReplaceLogic;
import sunset.gui.util.SunsetBundle;

public class SearchReplaceLogic implements ISearchReplaceLogic {
	
	private int matchStart;
	private int matchEnd;
	private String message;
	
	public SearchReplaceLogic() {
	}

	@Override
	public boolean search(String text, String pattern, int fromIndex, boolean bMatchCase, boolean bWrapAround) {
		matchStart = -1;
		
		if (bMatchCase) {
			matchStart = text.indexOf(pattern, fromIndex);
		} else {
			matchStart = text.toLowerCase().indexOf(pattern.toLowerCase(), fromIndex);
		}
		
		// if not found from pos, pos was not beginning (0), and wrap around is activated, search again from 0
		if (matchStart == -1 && fromIndex != 0 && bWrapAround) {
			if (bMatchCase) {
				matchStart = text.indexOf(pattern, 0);
			} else {
				matchStart = text.toLowerCase().indexOf(pattern.toLowerCase(), 0);
			}
		}
		
		if (matchStart != -1) {
			matchEnd = matchStart + pattern.length();
		}
		
		generateMessage(pattern, matchStart != -1);
		
		return matchStart != -1;
	}
	
	@Override
	public boolean searchRegex(String text, String pattern, int fromIndex, boolean bMatchCase, boolean bWrapAround, boolean bDotAll) {
		try {
			final int flags = (bMatchCase ? 0 : Pattern.CASE_INSENSITIVE) | (bDotAll ? Pattern.DOTALL : 0);
			Pattern p = Pattern.compile(pattern, flags);
			Matcher m = p.matcher(text);

			if (m.find(fromIndex) || bWrapAround && fromIndex != 0 && m.find(0)) {
				matchStart = m.start();
				matchEnd = m.end();
			} else {
				matchStart = -1;
			}
		} catch (PatternSyntaxException e) {	// bad regular expression specified 
			message = e.getMessage();
			return false;
		}
		
		generateMessage(pattern, matchStart != -1);
		
		return matchStart != -1;
	}
	
	/**
	 * Generates a message depending on the search outcome (success/failure)
	 * @param pattern the pattern to search for
	 * @param bSuccess the flag indicating if the search was successful or not
	 */
	private void generateMessage(String pattern, boolean bSuccess) {
		if (SunsetBundle.getInstance().getLocale().getLanguage().equals(new Locale("en").getLanguage())) {
			message = "\"" + pattern + "\"" + (bSuccess ? " found at line " : " not found");
		} else if (SunsetBundle.getInstance().getLocale().getLanguage().equals(new Locale("de").getLanguage())) {
			message = "\"" + pattern + "\"" + (bSuccess ? " gefunden in Zeile " : " nicht gefunden");
		}
	}
	
	@Override
	public int getStart() {
		return matchStart;
	}
	
	@Override
	public int getEnd() {
		return matchEnd;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
