#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Oct 18 10:22:10 2021

@author: christian
"""

import numpy as np
from matplotlib import pyplot as plt
# %matplotlib qt




def sell_up_probability(x: np.array) -> np.array:
    return 0.5 * np.exp(1.0 - 0.1 * xs) + np.exp(3.0 - 0.3 * xs)



if __name__ == "__main__":
    xs = np.arange(9, 30, step=0.01)
    ys = sell_up_probability(xs)
    plt.plot(xs, ys)
    plt.grid(True)
    plt.title("Sell-up probability curve")
    plt.xlabel("Price")
    plt.ylabel("Sell-up probability")
    plt.xlim(11, 30)
    plt.ylim(0, 1)
    plt.show()
