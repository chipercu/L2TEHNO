package l2open.extensions.scripts;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.eclipse.jdt.internal.compiler.tool.EclipseFileManager;

import l2open.util.GArray;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.logging.Logger;
/**
 * Класс компиляции внешних Java файлов<br>
 * В качестве компилятора используется Eclipse Java Compiler
 * 
 * @author G1ta0
 */
public class Compiler
{
	private static Compiler _instance;

	public static Compiler getInstance()
	{
		if(_instance == null)
			_instance = new Compiler();
		return _instance;
	}
	private static final Logger _log = Logger.getLogger(Compiler.class.getName());

	private static final JavaCompiler javac = new EclipseCompiler();

	private final DiagnosticListener<JavaFileObject> listener = new DefaultDiagnosticListener();
	private final StandardJavaFileManager fileManager = new EclipseFileManager(Locale.getDefault(), Charset.defaultCharset());

	public MemoryClassLoader classLoader = new MemoryClassLoader();
	private final JavaMemoryFileManager memFileManager = new JavaMemoryFileManager(fileManager, classLoader);

	public boolean compile(File[] files, PrintStream err)
	{
		// javac options
		List<String> options = new ArrayList<String>();
		options.add("-Xlint:all");
		options.add("-warn:none");
		//options.add("-g:none");
		options.add("-g");
		//options.add("-deprecation");

		Writer writer = new StringWriter();
		JavaCompiler.CompilationTask compile = javac.getTask(writer, memFileManager, listener, options, null, fileManager.getJavaFileObjects(files));

		if(compile.call())
			return true;

		return false;
	}

	public boolean compile(GArray<File> files, PrintStream err)
	{
		return compile(files.toArray(new File[files.size()]), err);
	}

	public boolean compile(File file, PrintStream err)
	{
		return compile(new File[] { file }, err);
	}

	public MemoryClassLoader getClassLoader()
	{
		return classLoader;
	}

	private class DefaultDiagnosticListener implements DiagnosticListener<JavaFileObject>
	{
		@Override
		public void report(Diagnostic<? extends JavaFileObject> diagnostic)
		{
			_log.warning(diagnostic.getSource().getName() + (diagnostic.getPosition() == Diagnostic.NOPOS ? "" : ":" + diagnostic.getLineNumber() + "," + diagnostic.getColumnNumber()) + ": " + diagnostic.getMessage(Locale.getDefault()));
		}
	}
	/******************************************/
	public static class JavaMemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager>
	{
		private final MemoryClassLoader classLoader;

		public JavaMemoryFileManager(StandardJavaFileManager fileManager, MemoryClassLoader specClassLoader)
		{
			super(fileManager);
			classLoader = specClassLoader;
		}

		@Override
		@SuppressWarnings("unused")
		public JavaFileObject getJavaFileForOutput(Location location, String name, JavaFileObject.Kind kind, FileObject sibling) throws IOException
		{
			MemoryJavaByteCode byteCode = new MemoryJavaByteCode(name);
			classLoader.addClass(byteCode);
			return byteCode;
		}
	}
	/******************************************/
	public static class MemoryClassLoader extends ClassLoader
	{
		public final HashMap<String, MemoryJavaByteCode> byteCodes = new HashMap<String, MemoryJavaByteCode>();
		public final ClassLoader cl = MemoryClassLoader.class.getClassLoader();

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException
		{
			//super.findClass(name);
			MemoryJavaByteCode byteCode = byteCodes.get(name);
			if(byteCode == null)
				throw new ClassNotFoundException(name);
			byte[] bytecode = byteCode.getBytes();
			//cl.defineClass(name, bytecode, 0, bytecode.length);
			return defineClass(name, bytecode, 0, bytecode.length);
			//return cl.defineClass(name, bytecode, 0, bytecode.length);
			//return cl.defineClass1(name, bytecode, 0, bytecode.length, null, null, true);
		}

		public void addClass(MemoryJavaByteCode code)
		{
			byteCodes.put(code.getName(), code);
		}
	}
	/******************************************/
	public static class MemoryJavaByteCode extends SimpleJavaFileObject
	{
		private ByteArrayOutputStream oStream;
		private final String className;

		public MemoryJavaByteCode(String name)
		{
			super(URI.create("byte:///" + name), Kind.CLASS);
			className = name;
		}

		@Override
		public OutputStream openOutputStream()
		{
			oStream = new ByteArrayOutputStream();
			return oStream;
		}

		public byte[] getBytes()
		{
			return oStream.toByteArray();
		}

		@Override
		public String getName()
		{
			return className;
		}
	}
}