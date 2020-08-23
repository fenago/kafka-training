
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
