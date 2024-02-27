package ffapl.lib;

import ffapl.lib.interfaces.ISymbol;
import ffapl.lib.interfaces.IToken;
import ffapl.types.FFaplRecord;
import ffapl.types.FFaplTypeCrossTable;
import ffapl.types.Type;

/**
 * Represents a Symbol of FFapl
 * @author Alexander Ortner
 * @version 1.0
 *
 */
public class FFaplSymbol implements ISymbol {

	//the kind of this symbol
	private int _kind;
	//the token of this symbol
	private IToken _token;
	//the symbol next to this symbol
	private ISymbol _next;
	//the type of the symbol
	private Type _type;
	//specifies if Symbol is global
	private boolean _isglobal;
	//specifies if Symbol is a reference
	private boolean _isreference;
	//name of the Symbol
	private String _name;
	//pointer to the child
	private ISymbol _local;
	//pointer to the father
	private ISymbol _scope;
	//level of scope
	private int _level;
	//do not look above in case of scoping
	private boolean _isshielded;
	//specifies if the symbol is readonly
	private boolean _readonly;
	//specifies if the symbol is initialized 
	private boolean _isinitialized;
	//the offset of the Symbol in memory
	private int _offset;
	
	/**
	 * Constructor
	 * @param name the name of the Symbol
	 * @param token the token of the Symbol
	 */
	public FFaplSymbol(String name, IToken token){
		this(name, token, null);
	}
	
	public FFaplSymbol(String name, int kind){
		this(name, null, null, kind);
	}
	
	/**
	 * Constructor
	 * @param name the name of the Symbol
	 * @param token the token of the Symbol
	 * @param type the type of the Symbol
	 */
	public FFaplSymbol(String name, IToken token, Type type){
		this(name, token, type, -1);		
	}
	
	/**
	 * Constructor
	 * @param name
	 * @param token
	 * @param kind
	 */
	public FFaplSymbol(String name, IToken token, int kind){
		 this(name, token, null, kind);	
	}
	
	/**
	 * contructor
	 * @param name
	 * @param token
	 * @param type
	 * @param kind
	 */
	public FFaplSymbol(String name, IToken token, Type type, int kind){
		_token = token;
		_name = name;
		_kind = kind;
		_type = type;
		_next = null;
		_level = -1;
		_isglobal = false;
		_isshielded = false;
		_readonly = _kind == ISymbol.CONSTANT;
		_isinitialized = ( _kind == ISymbol.PARAMETER || _type instanceof FFaplRecord);
		_offset = Integer.MIN_VALUE;
	}	
	
	@Override
	public int getKind() {
		return _kind;
	}

	@Override
	public String getKindStr() {
		return ISymbol.kindArray[_kind];
	}

	@Override
	public ISymbol scope(){
		return _scope;
	}
	
	@Override
	public ISymbol local(){
		return _local;
	}
	
	@Override
	public ISymbol next() {
		return _next;
	}

	@Override
	public Type getType() {
		return _type;
	}

	@Override
	public IToken getToken() {
		return _token;
	}

	@Override
	public boolean isGlobal() {
		return _isglobal;
	}

	@Override
	public boolean isReference() {
		return _isreference;
	}
	
	@Override
	public boolean readonly() {
		return _readonly;
	}

	@Override
	public void setReadonly(boolean readonly) {
		_readonly = readonly;		
	}

	@Override
	public void setGlobal(boolean isglobal) {
		_isglobal = isglobal;
	}

	@Override
	public void setKind(int kind) {
		_kind = kind;
	}

	public void setScope(ISymbol symbol){
		_scope = symbol;
	}
	@Override
	public void setNext(ISymbol symbol) {
		_next = symbol;
	}
	
	@Override
	public void setLocal(ISymbol symbol){
		_local = symbol;
	}
	
	public void setLevel(int level){
		_level = level;
	}

	@Override
	public void setReference(boolean isreference) {
		_isreference = isreference;
	}

	@Override
	public void setType(Type type) {
		_type = type;
	}

	@Override
	public String getName() {
		return _name;
	}
	
	@Override
	public String getID() {
		//return _name.toLowerCase(); //not case sensitive
		return _name; //case sensitive
	}
	
	@Override
	public int level(){
		return _level;
	}

	@Override
	public boolean equals(ISymbol symbol) {
		boolean result = false;
		
		switch(this.getKind()){
		//TODO überarbeiten für Parameter etc.
			case ISymbol.PARAMETER:
				//if(symbol.getKind() == ISymbol.PARAMETER){
				//	result = symbol.getType().equals(this.getType());
				//}else{
					result = symbol.getID().equals(this.getID());
				//}
				break;
				
			case ISymbol.VARIABLE:
				    if(symbol.getKind() == ISymbol.VARIABLE ||
				    		symbol.getKind() == ISymbol.PARAMETER ||
				    		symbol.getKind() == ISymbol.CONSTANT){
				    	result = symbol.getID().equals(this.getID());
				    }
				break;
				
			case ISymbol.FUNCTION:
				    result = controlFuncProc(symbol);
				break;
				
			case ISymbol.PROCEDURE:
				    result = controlFuncProc(symbol);
				break;
				
			case ISymbol.CONSTANT:
				    if(symbol.getKind() == ISymbol.VARIABLE ||
				    		symbol.getKind() == ISymbol.PARAMETER ||
				    		symbol.getKind() == ISymbol.CONSTANT){
				    	result = symbol.getID().equals(this.getID());
				    }
				break;
			case ISymbol.PROGRAM:
				    result = symbol.getID().equals(this.getID());
				break;
		}
		return result;
	}
	
	@Override
	public boolean similarTo(ISymbol symbol){
		if(symbol.getKind() == ISymbol.BLOCK || this.getKind() == ISymbol.BLOCK){
			return false;
		}else{
			return symbol.getID().equals(this.getID());
		}
	}

	/**
	 * Counts the parameters of a function or procedure. 
	 * @param symbol
	 * @return -1 if the <Code> symbol </Code> is not a procedure or function 
	 */
	private int countParams(ISymbol symbol){
		ISymbol tmp;
		int counter = -1;
		
		if ( symbol.getKind() == ISymbol.PROCEDURE || 
				symbol.getKind() == ISymbol.FUNCTION ){
			counter = 0;
			tmp = symbol.local();//goto child
			while(tmp != null && tmp.getKind() == ISymbol.PARAMETER){
				counter = counter + 1;
				tmp = tmp.next();
			}
		}

		return counter;
	}
	/**
	 * Controls if the procedure or function is equal (name and structure);
	 * @param symbol
	 * @return
	 */
	private boolean controlFuncProc(ISymbol symbol){
		boolean result;
		ISymbol param1, param2;
		int amount;
		
		if ( symbol.getKind() == ISymbol.PROCEDURE || 
				symbol.getKind() == ISymbol.FUNCTION ){
			
			if( symbol.getID().equals(this.getID()) ){
				amount = countParams(this);
				if(amount == countParams(symbol)){
					result = true;
					param1 = this.local();
					param2 = symbol.local();
					
					while(result && amount > 0){
						if((param1.getType().typeID() == FFaplTypeCrossTable.FFAPLINTEGER ||
								param1.getType().typeID() == FFaplTypeCrossTable.FFAPLPRIME) &&
								(param2.getType().typeID() == FFaplTypeCrossTable.FFAPLINTEGER ||
										param2.getType().typeID() == FFaplTypeCrossTable.FFAPLPRIME)){
							//integer and Prime in func or proc are equal -> implicit cast
							result = true;
						}else{
							result = param1.getType().equals(param2.getType());
						}
						
					 	param1 = param1.next();
					 	param2 = param2.next();
						amount = amount - 1;
					}
				}else{//not the same amount of params
					result = false;
				}					
					
			}else{//different names
				result = false; 
			}
		}else{// symbol is not a Procedure or function
			result = false;
		}
		return result;
	}

	@Override
	public boolean isShielded() {
		return _isshielded;
	}

	@Override
	public void setShielded(boolean isShielded) {
		_isshielded = isShielded;		
	}
	
	@Override
	public boolean isInitialized() {
		return _isinitialized;
	}

	@Override
	public void setInitialized(boolean isinitialized) {
		_isinitialized = isinitialized;		
	}
	
	@Override
	public ISymbol clone(){
		ISymbol symbol = new FFaplSymbol(_name, _token, _type, _kind);
		symbol.setGlobal(_isglobal);
		symbol.setLevel(_level);
		symbol.setLocal(_local);
		symbol.setNext(_next);
		symbol.setReference(_isreference);
		symbol.setScope(_scope);
		symbol.setShielded(_isshielded);
		symbol.setInitialized(_isinitialized);
		return symbol;
	}
	
	@Override
	public String toString(){
		return this.getID();
	}

	@Override
	public void setOffset(int offset) {
		this._offset = offset;
	}

	@Override
	public int getOffset() {
		return _offset;
	}

	@Override
	public boolean setOffset() {
		return !(_offset == Integer.MIN_VALUE);
	}

	@Override
	public void resetOffset() {
		this._offset = Integer.MIN_VALUE;		
	}

	

	
}
