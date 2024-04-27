
mainclass = tolt.server.Main
outfile = ./build/tolt-server.jar
classp = ./build/classes/

output:
	find ./src/ -name "*.java" | xargs javac -d $(classp)
	jar -cfve $(outfile) $(mainclass) -C $(classp) .

clean:
	rm -rv $(classp)/ $(outfile)

run:
	cp $(outfile) ./test/
	cd ./test/;java -jar *.jar

lines:
	git ls-files | grep '.java' | xargs wc -l
