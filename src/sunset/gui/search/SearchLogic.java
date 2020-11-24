package sunset.gui.search;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import sunset.gui.search.interfaces.ISearchLogic;
import sunset.gui.util.SunsetBundle;

public class SearchLogic implements ISearchLogic {
	
	private int _matchStart;
	private int _matchEnd;
	private String _message;
	
	@Override
	public boolean search(String text, String pattern, int fromIndex, boolean bMatchCase, boolean bWrapAround) {
		_matchStart = -1;
		
		if (bMatchCase) {
			_matchStart = text.indexOf(pattern, fromIndex);
		} else {
			_matchStart = text.toLowerCase().indexOf(pattern.toLowerCase(), fromIndex);
		}
		
		// if not found from pos, pos was not beginning, and wrap around is activated, search again from 0
		if (_matchStart == -1 && fromIndex != 0 && bWrapAround) {
			if (bMatchCase) {
				_matchStart = text.indexOf(pattern, 0);
			} else {
				_matchStart = text.toLowerCase().indexOf(pattern.toLowerCase(), 0);
			}
		}
		
		if (_matchStart != -1) {
			_matchEnd = _matchStart + pattern.length();
		}
		
		generateMessage(pattern, _matchStart != -1);
		
		return _matchStart != -1;
	}
	
	@Override
	public boolean searchRegex(String text, String pattern, int fromIndex, boolean bMatchCase, boolean bWrapAround, boolean bDotAll) {
		Matcher m = getMatcher(text, pattern, bMatchCase, bDotAll);
		
		if (m != null) {
			if (m.find(fromIndex) || bWrapAround && fromIndex != 0 && m.find(0)) {
				_matchStart = m.start();
				_matchEnd = m.end();
			} else {
				_matchStart = -1;
			}

			generateMessage(pattern, _matchStart != -1);
			return _matchStart != -1;
		}
		
		return false;
	}
	
	/**
	 * Returns a Matcher object corresponding to the specified text and regular expression pattern
	 * @param text the text the matcher is based on
	 * @param pattern the pattern the matcher is based on
	 * @param bMatchCase the flag indicating if case sensitive regular expression matching is required
	 * @param bDotAll the flag indicating if . should match newline characters in the regular expression
	 * @return a Matcher object corresponding to the given parameters, null if an invalid regular expression was specified
	 */
	private Matcher getMatcher(String text, String pattern, boolean bMatchCase, boolean bDotAll) {
		try {
			final int flags = (bMatchCase ? 0 : Pattern.CASE_INSENSITIVE) | (bDotAll ? Pattern.DOTALL : 0);
			Pattern p = Pattern.compile(pattern, flags);
			Matcher m = p.matcher(text);
			
			return m;
		} catch (PatternSyntaxException e) {	// bad regular expression pattern specified 
			_message = e.getMessage();
			return null;
		}
	}
	
	/**
	 * Generates a message depending on the search outcome (success/failure)
	 * @param pattern the pattern to search for
	 * @param bSuccess the flag indicating if the search was successful or not
	 */
	private void generateMessage(String pattern, boolean bSuccess) {
		if (SunsetBundle.getInstance().getLocale().getLanguage().equals(new Locale("en").getLanguage())) {
			_message = "\"" + pattern + "\"" + (bSuccess ? " found at line " : " not found from line ");
		} else if (SunsetBundle.getInstance().getLocale().getLanguage().equals(new Locale("de").getLanguage())) {
			_message = "\"" + pattern + "\"" + (bSuccess ? " gefunden in Zeile " : " nicht gefunden ab Zeile ");
		}
	}
	
	@Override
	public int getStart() {
		return _matchStart;
	}
	
	@Override
	public int getEnd() {
		return _matchEnd;
	}
	
	@Override
	public String getMessage() {
		return _message;
	}

	@Override
	public boolean matchesRegex(String text, String pattern, boolean bMatchCase, boolean bDotAll) {
		Matcher m = getMatcher(text, pattern, bMatchCase, bDotAll);
		
		if (m != null) {
			return m.matches();
		}
		
		return false;
	}

	@Override
	public boolean equals(String text, String pattern, boolean bMatchCase) {
		if (bMatchCase) {
			return text.equals(pattern);
		} else {
			return text.toLowerCase().equals(pattern.toLowerCase());
		}
	}
}