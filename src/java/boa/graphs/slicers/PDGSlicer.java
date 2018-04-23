/*
 * Copyright 2018, Robert Dyer, Mohd Arafat
 *                 and Bowling Green State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package boa.graphs.slicers;

import boa.graphs.pdg.PDG;
import boa.graphs.pdg.PDGNode;
import boa.types.Ast.*;
import org.apache.hadoop.io.nativeio.NativeIO;

import java.util.*;

/**
 * A forward slicer based on PDG
 *
 * @author marafat
 */

public class PDGSlicer {

    private Set<PDGNode> entryNodes = new HashSet<PDGNode>();
    private Set<PDGNode> slice = new HashSet<PDGNode>();

    public PDGSlicer(Method m, PDGNode n) throws Exception {
        entryNodes.add(n);
        getSlice(new PDG(m, true));
    }

    public PDGSlicer(Method m, PDGNode[] n) throws Exception {
        entryNodes.addAll(Arrays.asList(n));
        getSlice(new PDG(m, true));
    }

    public PDGSlicer(Method m, int nid) throws Exception {
        PDG pdg = new PDG(m, true);
        PDGNode n = pdg.getNode(nid);
        entryNodes.add(n);
        getSlice(pdg);
    }

    public PDGSlicer(Method m, Integer[] nids) throws Exception {
        PDG pdg = new PDG(m, true);
        for (Integer i: nids) {
            entryNodes.add(pdg.getNode(i));
        }
        getSlice(pdg);
    }

    // Getters
    public Set<PDGNode> getEntryNodes() {
        return entryNodes;
    }

    public Set<PDGNode> getSlice() {
        return slice;
    }

    /**
     * Traverse the pdg to collect the slices
     *
     * @param pdg program dependence graph
     */
    private void getSlice(PDG pdg) {
        Stack<PDGNode> nodes = new Stack<PDGNode>();
        nodes.addAll(entryNodes);

        while (nodes.size() != 0) {
            PDGNode node = nodes.pop();
            slice.add(node);
            for (PDGNode succ: node.getSuccessors())
                if (!slice.contains(succ))
                    nodes.push(succ);
        }
    }
}
