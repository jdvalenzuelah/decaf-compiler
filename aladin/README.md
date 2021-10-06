# Aladin: A jasmin assembly generator

## A jasmin hello world generated with aladin

```kotlin
ClassSpec.builder("HelloWorld")
    .superClass(Java.lang.Object)
    .addModifier(ClassAccessModifiers.PUBLIC)
    .addMethod(
        MethodSpec.constructorBuilder()
            .addModifier(MethodAccessModifier.PUBLIC)
            .addCodeBlock(
                CodeBlockSpec.builder()
                    .aload0()
                    .invokeNonVirtual(Java.lang.init, MethodDescriptor(emptyList(), TypeDescriptor.Void))
                    .returns()
                    .build()
            )
            .build()
    )
    .addMethod(
        MethodSpec.builder(null, "main")
            .addModifier(MethodAccessModifier.PUBLIC)
            .addModifier(MethodAccessModifier.STATIC)
            .addArgument(TypeDescriptor.String.asArray())
            .addReturnType(TypeDescriptor.Void)
            .limitStack(2)
            .addCodeBlock(
                CodeBlockSpec.builder()
                    .getStatic(Java.lang.System.pckg, Java.lang.System.out)
                    .ldc(Constant.Str("Hello world!"))
                    .invokeVirtual(Java.io.println, MethodDescriptor(listOf(TypeDescriptor.String), TypeDescriptor.Void))
                    .returns()
                    .build()
            )
            .build()
    )
    .build()
```

This will generate the following jasmini assembly
```jasmin
.source HelloWorld.j
.class public HelloWorld
.super java/lang/Object


.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 2

	getstatic java/lang/System/out Ljava/io/PrintStream;
	ldc "Hello world!"
	invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
	return
.end method
```

## Generating `.class` files

Aladin is bundled with jasmin assembler to generate `.class` bytecode files 

### Example:
```kotlin
TODO()
```