<?php

$gaz=[];

$remove=array_flip(["al","din","cu","și","sau","la","mai","pe","prin","lui","către","care","pentru"]);

function startsWith( $haystack, $needle ) {
     $length = strlen( $needle );
     return substr( $haystack, 0, $length ) === $needle;
}

function endsWith( $haystack, $needle ) {
    $length = strlen( $needle );
    if( !$length ) {
        return true;
    }
    return substr( $haystack, -$length ) === $needle;
}

function processFolder($dir){
if (is_dir($dir)) {
    if ($dh = opendir($dir)) {
        while (($file = readdir($dh)) !== false) {
            $path="$dir/$file";
            if(endsWith($path,".ann"))processFile($path);
        }
        closedir($dh);
    }else echo "Cannot open folder [$dir]\n";
}else echo "Folder not found [$dir]\n";
}


function processFile($fname){
    global $gaz;

    $fp=fopen($fname,"r");
    while(!feof($fp)){
        $line=fgets($fp);
        if($line===false)break;

        $line=trim($line);
        $data=explode("\t",$line);
        if(count($data)!==3)continue;
        
        $words=explode(" ",$data[2]);
        foreach($words as $w){
            $w=trim($w,", \n\r\t\v\0()\"«»");
            if(strlen($w)<2)continue;
            $w=mb_strtolower($w);
            if(is_numeric($w))continue;
            if(isset($remove[$w]))continue;
            $gaz[$w]=true;
        }
    }
    fclose($fp);
}

function addJRC(){
    global $gaz;
    $fp=fopen("jrc-names/entities.txt","r");
    while(!feof($fp)){
        $line=fgets($fp);
        if($line===false)break;
        $line=trim($line);
        $data=explode("\t",$line);
        if(count($data)!=4)continue;
        $name=mb_strtolower($data[3]);
        foreach(explode("+",$name) as $n)
            $gaz[$n]=true;
    }
    fclose($fp);
}

function addGeonames(){
    global $gaz;
    $fp=fopen("geonames/RO.txt","r");
    while(!feof($fp)){
        $line=fgets($fp);
        if($line===false)break;
        $line=trim($line);
        $data=explode("\t",$line);
        if(count($data)<7)continue;
        if($data[6]!=='A' && $data[6]!=='P')continue;
        $name=mb_strtolower($data[1]);
        foreach(explode(" ",$name) as $n){
                $n=trim($n,"() \".");
                if(strlen($n)>0)
                    $gaz[$n]=true;
        }
        $name=mb_strtolower($data[2]);
        foreach(explode(",",$name) as $n1)
            foreach(explode(" ",$n1) as $n){
                $n=trim($n,"() \".");
                if(strlen($n)>0)
                    $gaz[$n]=true;
            }
    }
    fclose($fp);
}


//processFolder("data/train");

addJRC();
addGeonames();
file_put_contents("PharmaCoNER-Tagger/data/gazetteers/legalnero_all.gaz",implode("\n",array_keys($gaz)));
