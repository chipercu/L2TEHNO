package l2open.extensions.scripts;

import l2open.util.GArray;

import javax.tools.*;
import java.io.*;
import java.nio.charset.Charset;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.eclipse.jdt.internal.compiler.tool.EclipseFileManager;

public class Compiler
{
	private static final Logger _log = Logger.getLogger(Compiler.class.getName());

	private static Compiler _instance;

	public static Compiler getInstance()
	{
		if(_instance == null)
			_instance = new Compiler();
		return _instance;
	}

	private static final JavaCompiler javac = new EclipseCompiler();
	private final StandardJavaFileManager standartFileManager = new EclipseFileManager(Locale.getDefault(), Charset.defaultCharset());

	public MemoryClassLoader classLoader;

	public boolean compile(File[] files, PrintStream err)
	{
		classLoader = new MemoryClassLoader(); //(MemoryClassLoader) Compiler.class.getClassLoader();
		JavaMemoryFileManager fileManager = new JavaMemoryFileManager(standartFileManager, classLoader);
		DiagnosticCollector<JavaFileObject> diacol = new DiagnosticCollector<JavaFileObject>();

		List<String> options = new ArrayList<String>();
		options.add("-Xlint:all");
		options.add("-warn:none");
		options.add("-g");
		options.add("-1.8");

		if(javac.getTask(null, fileManager, diacol, options, null, standartFileManager.getJavaFileObjects(files)).call())
			return true;

		if(err != null)
			for(Diagnostic<? extends JavaFileObject> dia : diacol.getDiagnostics())
				err.println(dia);
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
	/******************************************/
	public static class MemoryClassLoader extends ClassLoader
	{
		public final HashMap<String, MemoryJavaByteCode> byteCodes = new HashMap<String, MemoryJavaByteCode>();
		//public final ClassLoader cl = MemoryClassLoader.class.getClassLoader();

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
			//return defineClass01(name, bytecode, 0, bytecode.length, null);
			//return cl.defineClass(name, bytecode, 0, bytecode.length);
			//return cl.defineClass1(name, bytecode, 0, bytecode.length, null, null, true);
		}

		public void addClass(MemoryJavaByteCode code)
		{
			byteCodes.put(code.getName(), code);
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
			MemoryJavaByteCode byteCode = new MemoryJavaByteCode(name.replace('/', '.').replace('\\', '.'), URI.create("file:///" + name.replace('.', '/').replace('\\', '/') + kind.extension));
			classLoader.addClass(byteCode);
			return byteCode;
		}
	}
	/******************************************/
	public static class MemoryJavaByteCode extends SimpleJavaFileObject
	{
		private ByteArrayOutputStream oStream;
		private final String className;

		public MemoryJavaByteCode(String name, URI uri)
		{
			super(uri, Kind.CLASS);
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