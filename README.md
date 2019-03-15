# UltraSvgz

[<img alt="Travis CI Build Status" src="http://img.shields.io/travis/thomasWeise/ultraSvgz/master.svg" height="20"/>](http://travis-ci.org/thomasWeise/ultraSvgz/)

This tool converts Scalable Vector Graphics ([`svg`](http://en.wikipedia.org/wiki/SVG)s) into the `svgz` format, a [`gzip`](http://en.wikipedia.org/wiki/Gzip) compressed version thereof.
The goal is that the tool first performs pre-processing stesp in the hope to reduce the size of the `svg`, which is basically an [XML](http://en.wikipedia.org/wiki/XML) format, by removing useless spaces and hopefully eventually doing clever stuff to re-arrange and condense the contents in a lossless fashion.
For this, we use a wide variety of tools, even including minimizing the file via free online services by uploading it and downloading the result (requires internet connection).
Then, it takes the resulting data and pipes it through our [UltraGzip](http://github.com/thomasWeise/ultraGzip) tool to achieve a strong compression.

## 1. How to use `UlrtraSvgz` from the Command Line

Run `java -jar ultraGzip-0.8.0-full.jar ARGUMENTS`

The following arguments are supported:

- `help` print the help screen and all arguments
- `svgIntensity=0...10` is the intensity of the svg processing. It ranges from 0 to 10, with 5 as default. UltraSvgz is a bit slow and uses many tools. 5 is a good default setting that will provide a very strong compression already. If you kick the setting up to 10, the tool will become slower.
- `gzipIntensity=0...10` is the intensity used for the gzip compression. It ranges from 0 to 10, with 5 as default. UltraGzip is a bit slow and uses many tools. 5 is a good default setting that will provide a very strong compression already. If you kick the setting up to 10, the tool will become very slow, but maybe can squeeze out another 2 or 3 bytes.
- `in=/path/to/file` the path to the file with the source data to be compressed
- `si` compress contents written to `stdin` instead of a file. You must specify either `in=...` or the `si` option. 
- `out=/path/to/file` the path to the file where the compressed data should be written to; if omitted and `in` is specified, an output file of the same name as the input will be generated in the input file's directory, but with an `.svgz` suffix.
- `so` write the compressed contents to `stdout` instead of a file. You must specify either `out=...` or the `so` option.
- `help` print the help screen

## 2. Requirements

If you are on Linux, the following utilities can improve the compression which can be achieved by
installing the following additional programs:

* [gzip](http://en.wikipedia.org/wiki/Gzip) (should already be installed)
* [AdvanceCOMP](https://en.wikipedia.org/wiki/AdvanceCOMP) (`sudo apt-get install advancecomp`)
* [7-zip](http://www.7-zip.org/) (`sudo apt-get install p7zip-full`)
* [zopfli](http://en.wikipedia.org/wiki/Zopfli) (`sudo apt-get install zopfli`)
* [pigz](http://zlib.net/pigz/) (`sudo apt-get install pigz`)
* [Python 3's gzip](http://docs.python.org/3/library/gzip.html) library, which should normally be installed, too
* [librsvg](http://wiki.gnome.org/Projects/LibRsvg) which can be installed via `sudo apt-get install librsvg2-bin librsvg2-common`.

An internet connection is needed to use the online minifiers (currently only <https://www.svgminify.com>)

## 3. Use as `UltraSvgz` in your Java Code

You can import the class `UltraSvgz` and then create a job which takes an array of `byte` as input data and returns an array of `byte` as compression result. The job implements `Callable<byte[]>`.

## 4. Licensing

This software uploads your SVG to <https://www.svgminify.com>, which is a free online svg minifying service provided Spikerog SAS, 15 Grand Rue, 11800 Laure Minervois, France.
For this step, obviously the terms and conditions of this service and company hold.

This software directly uses [scour](http://github.com/scour-project/scour) as one of
its internal components.
Although our software here is GPL-3 licensed, scour is under the Apache License Version 2.0.
The license of scour is contained in file LICENSE-scour.txt.

This software uses the linux binary version of [svgcleaner](http://github.com/RazrFalcon/svgcleaner) as one of its internal components.
Although our software here is GPL-3 licensed, svgcleaner is under the GPL-2.
We obtained permission from Mr. Reizner via email to include his binaries this way.
The license of svgcleaner is contained in file LICENSE-svgcleaner.txt.

This software indirectly uses [JZlib](http://www.jcraft.com/jzlib/) as one of its internal `gzip` utilities. Although our software here is `GPL` licensed, JZlib is under a [BSD-style license](http://www.jcraft.com/jzlib/LICENSE.txt).
The GPL licensing of our software therefore _only_ applies to our own code, while the code of JZLib follows said [BSD-style license](http://www.jcraft.com/jzlib/LICENSE.txt), which is included in file LICENSE-JZlib.txt.

The binary distribution of our software may include binary versions of the above tools

## 5. Contact

If you have any questions or suggestions, please contact
[Prof. Dr. Thomas Weise](http://iao.hfuu.edu.cn/team/director) of the
[Institute of Applied Optimization](http://iao.hfuu.edu.cn/) at
[Hefei University](http://www.hfuu.edu.cn) in
Hefei, Anhui, China via
email to [tweise@hfuu.edu.cn](mailto:tweise@hfuu.edu.cn) with CC to [tweise@ustc.edu.cn](mailto:tweise@ustc.edu.cn).
