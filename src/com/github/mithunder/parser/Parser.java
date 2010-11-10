package com.github.mithunder.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.mithunder.node.Node;

public class Parser {

	public List<Node> parse(String file) {
		return parse(new File(file));
	}
	public List<Node> parse(File file) {
		ArrayList<Node> list = new ArrayList<Node>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String read = "";
			while(!read.equals("NODE_COORD_SECTION")) {
				read = in.readLine().trim();
				//TODO: Something more clever than just print to screen
//				System.out.println(read);
			}
			while(!read.equals("EOF")) {
				read = in.readLine();
				if(read == null || read.equals("")) {
					break;
				}
				read = read.trim();
				String[] node = read.split(" ");
				if(node.length >= 3) {
					int id = -1;
					int x = -1;
					int y = -1;
					for(int i = 0; i < node.length; i++) {
						if(!node[i].equals("")) {
							if(id == -1) {
								id = i;
							} else if(x == -1) {
								x = i;
							} else {
								y = i;
								break;
							}
						}
					}
					list.add(new Node(Integer.parseInt(node[id]),Double.parseDouble(node[x]),Double.parseDouble(node[y])));
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
