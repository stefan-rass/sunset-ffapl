/* Generated by: ParserGeneratorCC: Do not edit this line. JavaCharStream.java Version 1.1 */
/* ParserGeneratorCCOptions:SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package ffapl;

/**
 * An implementation of interface CharStream, where the stream is assumed to
 * contain only ASCII characters (with java-like unicode escape processing).
 */
public
class JavaCharStream extends AbstractCharStream
{
  /** Predefined buffer size */
  protected static final int NEXTCHAR_BUF_SIZE = 4096;

  protected char[] nextCharBuf;
  protected int nextCharInd = -1;
  protected java.io.Reader inputStream;

  @Override
  protected int streamRead(final char[] buffer, final int offset, final int len) throws java.io.IOException
  {
    return inputStream.read (buffer, offset, len); 
  }
  
  @Override
  protected void streamClose() throws java.io.IOException
  {
    inputStream.close (); 
  }

  @Override
  protected void fillBuff() throws java.io.IOException
  {
    if (maxNextCharInd == NEXTCHAR_BUF_SIZE)
      maxNextCharInd = nextCharInd = 0;

    try
    {
      final int i = inputStream.read(nextCharBuf, maxNextCharInd, NEXTCHAR_BUF_SIZE - maxNextCharInd);
      if (i == -1)
      {
        inputStream.close();
        throw new java.io.IOException();
      }
      maxNextCharInd += i;
    }
    catch(final java.io.IOException ex)
    {
      if (bufpos != 0)
      {
        --bufpos;
        backup(0);
      }
      else
      {
        bufline[bufpos] = line;
        bufcolumn[bufpos] = column;
      }
      throw ex;
    }
  }

  protected char readByte() throws java.io.IOException
  {
    nextCharInd++;
    if (nextCharInd >= maxNextCharInd)
      fillBuff();

    return nextCharBuf[nextCharInd];
  }

  /** @return starting character for token. */
  public char beginToken() throws java.io.IOException
  {
    if (inBuf > 0)
    {
      --inBuf;

      if (++bufpos == bufsize)
        bufpos = 0;

      tokenBegin = bufpos;
      return buffer[bufpos];
    }

    tokenBegin = 0;
    bufpos = -1;

    return readChar();
  }

  protected void adjustBuffSize()
  {
    if (available == bufsize)
    {
      if (tokenBegin > 2048)
      {
        bufpos = 0;
        available = tokenBegin;
      }
      else
        expandBuff(false);
    }
    else
      if (available > tokenBegin)
        available = bufsize;
      else
        if ((tokenBegin - available) < 2048)
          expandBuff(true);
        else
          available = tokenBegin;
  }

  /** Read a character. */
  public char readChar() throws java.io.IOException
  {
    if (inBuf > 0)
    {
      --inBuf;
      ++bufpos;
      if (bufpos == bufsize)
        bufpos = 0;

      return buffer[bufpos];
    }

    ++bufpos;
    if (bufpos == available)
      adjustBuffSize();

    char c = readByte();
    buffer[bufpos] = c;
    if (c == '\\')
    {
      if (isTrackLineColumn()) 
        updateLineColumn(c);

      int backSlashCnt = 1;

      for (;;) // Read all the backslashes
      {
        ++bufpos;
        if (bufpos == available)
          adjustBuffSize();

        try
        {
          c = readByte();
          buffer[bufpos] = c;
          if (c != '\\')
          {
            if (isTrackLineColumn()) 
               updateLineColumn(c);
               
            // found a non-backslash char.
            if ((c == 'u') && ((backSlashCnt & 1) == 1))
            {
              if (--bufpos < 0)
                bufpos = bufsize - 1;

              break;
            }

            backup(backSlashCnt);
            return '\\';
          }
        }
        catch(final java.io.IOException e)
        {
	        // We are returning one backslash so we should only backup (count-1)
          if (backSlashCnt > 1)
            backup(backSlashCnt-1);

          return '\\';
        }

        if (isTrackLineColumn()) 
          updateLineColumn(c);
        backSlashCnt++;
      }

      // Here, we have seen an odd number of backslash's followed by a 'u'
      try
      {
        while ((c = readByte()) == 'u')
          ++column;
        
        buffer[bufpos] = c = (char)(hexval(c) << 12 |
                                    hexval(readByte()) << 8 |
                                    hexval(readByte()) << 4 |
                                    hexval(readByte()));

        column += 4;
      }
      catch(final java.io.IOException e)
      {
        throw new IllegalStateException("Invalid escape character at line " + line + " column " + column + ".");
      }

      if (backSlashCnt == 1)
        return c;

      backup(backSlashCnt - 1);
      return '\\';
    }

    // Not a backslash
    if (isTrackLineColumn()) 
      updateLineColumn(c);
    return c;
  }

  /** Constructor. */
  public JavaCharStream(final java.io.Reader dstream,
                        final int startline,
                        final int startcolumn,
                        final int buffersize)
  {
    super (startline, startcolumn, buffersize);
    nextCharBuf = new char[NEXTCHAR_BUF_SIZE];
    inputStream = dstream;
  }

  /** Constructor. */
  public JavaCharStream(final java.io.Reader dstream,
                        final int startline,
                        final int startcolumn)
  {
    this(dstream, startline, startcolumn, DEFAULT_BUF_SIZE);
  }

  /** Constructor. */
  public JavaCharStream(final java.io.Reader dstream)
  {
    this(dstream, 1, 1, DEFAULT_BUF_SIZE);
  }

  /** Reinitialise. */
  public void reInit(final java.io.Reader dstream)
  {
    reInit(dstream, 1, 1, DEFAULT_BUF_SIZE);
  }

  /** Reinitialise. */
  public void reInit(final java.io.Reader dstream,
                     final int startline,
                     final int startcolumn)
  {
    reInit(dstream, startline, startcolumn, DEFAULT_BUF_SIZE);
  }

  /** Reinitialise. */
  public void reInit(final java.io.Reader dstream,
                     final int startline,
                     final int startcolumn,
                     final int buffersize)
  {
    nextCharBuf = new char[NEXTCHAR_BUF_SIZE];
    nextCharInd = -1;
    inputStream = dstream;
    super.reInit (startline, startcolumn, buffersize);
  }
  
  /** Constructor. */
  public JavaCharStream(final java.io.InputStream dstream, 
                        final java.nio.charset.Charset encoding, 
                        final int startline,
                        final int startcolumn, 
                        final int buffersize)
  {
    this(new java.io.InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
  }

  /** Constructor. */
  public JavaCharStream(final java.io.InputStream dstream,
                        final java.nio.charset.Charset encoding,
                        final int startline,
                        final int startcolumn)
  {
    this(dstream, encoding, startline, startcolumn, DEFAULT_BUF_SIZE);
  }

  /** Constructor. */
  public JavaCharStream(final java.io.InputStream dstream, 
                        final java.nio.charset.Charset encoding)
  {
    this(dstream, encoding, 1, 1, DEFAULT_BUF_SIZE);
  }
  
  /** Reinitialise. */
  public void reInit(final java.io.InputStream dstream,
                     final java.nio.charset.Charset encoding)
  {
    reInit(dstream, encoding, 1, 1, DEFAULT_BUF_SIZE);
  }

  /** Reinitialise. */
  public void reInit(final java.io.InputStream dstream,
                     final java.nio.charset.Charset encoding, 
                     final int startline,
                     final int startcolumn)
  {
    reInit(dstream, encoding, startline, startcolumn, DEFAULT_BUF_SIZE);
  }
 
  /** Reinitialise. */
  public void reInit(final java.io.InputStream dstream, 
                     final java.nio.charset.Charset encoding, 
                     final int startline,
                     final int startcolumn,
                     final int buffersize)
  {
    reInit(new java.io.InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
  }
  
  @Override
  public void done ()
  {
    nextCharBuf = null;
    super.done ();
  }
}
/* ParserGeneratorCC - OriginalChecksum=ddea675aea2a49134b295869863e5814 (do not edit this line) */
