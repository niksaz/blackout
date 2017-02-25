
from string import Template

def generate(type):
    template = """
    @Test
    public void serialize${Type}Test() throws Exception {
        final int n = 10;
        final Random random = new Random();
        final ${type}[] arr = new ${type}[n];
        byte[] encoded;

        try (ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
             EfficientOutputStream os = new EfficientOutputStream(bytesOS)) {
            for (int i = 0; i < n; i++) {
                arr[i] = random.next${Type}();
                os.write${Type}(arr[i]);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (int i = 0; i < n; i++) {
                assertEquals(arr[i], is.read${Type}());
            }
        }
    }
    """

    return Template(template).substitute(type=type, Type=type.title())


def main():
    print(generate("boolean"))
    print(generate("byte"))
    print(generate("char"))
    print(generate("short"))
    print(generate("int"))
    print(generate("long"))
    print(generate("float"))
    print(generate("double"))


if __name__ == "__main__":
    main()
