
mainclass = tolt.server.Main
outfile = ./build/tolt-server.jar
classp = ./build/classes/

output:
	find ./src/ -name "*.java" | xargs javac -d $(classp)
	jar -cfve $(outfile) $(mainclass) -C $(classp) .

clean:
	rm -rv ./classes/ $(outfile)

run:
	java -jar $(outfile)
