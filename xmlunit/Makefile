LIBXMLUNIT=xmlunit.dll
LIBNUNIT=NUnit.Framework.dll
SRC=src/csharp/*.cs
TESTSRC=tests/csharp/*.cs

all: $(LIBXMLUNIT) tests

$(LIBXMLUNIT): $(SRC)
	mcs -out:$(LIBXMLUNIT) -target:library $(SRC) /r:$(LIBNUNIT)

test.exe: $(LIBXMLUNIT) $(TESTSRC)
	mcs -out:test.exe /r:$(LIBXMLUNIT) /r:$(LIBNUNIT) $(TESTSRC)

tests: test.exe
	mono test.exe
