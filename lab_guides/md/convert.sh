
read -p "Are you sure? " -n 1 -r
echo    # (optional) move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]
then
    # do dangerous stuff
	for i in *.md; do
		[ -f "$i" ] || break
		pdf=${i/.md/.pdf}
		echo  pandoc $i -f markdown -t html -s -o $pdf
		pandoc $i -f markdown -t html -s -o $pdf
	done
	
fi

### Convert to PDF
md-to-pdf  kafka-lab5-5.md  kafka-lab8-1.md  kafka-lab5-6.md  kafka-lab-8-3.md kafka_course_detailed_outline.md  kafka-lab5-7.md  kafka-lab-8-4.md kafka_course_notes.md kafka-lab5-8.md  kafka-lab-9.md kafka-avro-lab7-1.md  kafka-lab6-1.md  lab_overview.md kafka-lab2.md kafka-lab6-2.md  kafka-lab1.md kafka-lab3.md kafka-lab6-3.md  kafka-lab1-2.md kafka-lab5-1.md       kafka-lab6-4.md  lab-kafka-7-2.md kafka-lab5-2.md kafka-lab6-5.md kafka-lab5-3.md kafka-lab6-6.md  StreamsLab1.md kafka-lab5-4.md kafka-lab6-7.md  StreamsLab2.md
