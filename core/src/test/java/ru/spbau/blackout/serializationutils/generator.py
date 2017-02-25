
from string import Template

def generate(type, generator = "default", equals = "assertEquals"):
    if generator == "default":
        generator = Template("random.next${Type}()").substitute(Type=type.title())

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
                arr[i] = ${generator};
                os.write${Type}(arr[i]);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (int i = 0; i < n; i++) {
                ${equals}(arr[i], is.read${Type}());
            }
        }
    }"""

    return Template(template).substitute(type=type, Type=type.title(), generator=generator, equals=equals)


def main():
    print(generate("boolean"))
    print(generate("byte", "(byte) random.nextInt()"))
    print(generate("char", "(char) random.nextInt()"))
    print(generate("short", "(short) random.nextInt()"))
    print(generate("int"))
    print(generate("long"))
    print(generate("float", equals="Utils.floatEq"))
    print(generate("double", equals="Utils.floatEq"))
    print(generate("Vector2", "new Vector2(random.nextFloat(), random.nextFloat())"))


if __name__ == "__main__":
    main()
