#!/usr/bin/env python

import sys
from scour.scour import run, parse_args, getInOut, start

options = parse_args()
(input, output) = getInOut(options)
start(options, input, output)