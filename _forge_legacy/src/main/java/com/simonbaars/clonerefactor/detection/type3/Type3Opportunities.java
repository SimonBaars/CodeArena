package com.simonbaars.clonerefactor.detection.type3;

import java.util.List;
import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.settings.Settings;

public class Type3Opportunities implements Type3Calculation, CalculatesPercentages {
	private ListMap<Integer, FileLocations> opportunities = new ListMap<>();
	
	public void determineType3Opportunities(List<Sequence> clones) {
		for(int i = 0; i<clones.size(); i++) {
			FileLocations fl = new FileLocations(clones.get(i));
			if(opportunities.containsKey(fl.hashCode())) {
				 List<FileLocations> otherFls = opportunities.get(fl.hashCode());
				 for(FileLocations loc : otherFls) {
					 if(isType3(fl, loc)) {
						 clones.remove(fl.getSeq());
						 clones.remove(loc.getSeq());
						 clones.add(merge(fl.getLocs(), loc.getLocs()));
						 i--;
					 }
				 }
			} 
			opportunities.addTo(fl.hashCode(), fl);
		}
	}
	
	private Sequence merge(List<Location> fl1, List<Location> fl2) {
		Sequence seq = new Sequence();
		for(int i = 0; i<fl1.size(); i++) {
			seq.add(new Type3Location(fl1.get(i), fl2.get(i)));
		}
		return seq;
	}

	public boolean isType3(FileLocations fl1, FileLocations fl2) {
		return IntStream.range(0, fl1.getLocs().size()).anyMatch(i -> {
			Location l1 = fl1.getLocs().get(i);
			Location l2 = fl2.getLocs().get(i);
			if(!l1.getContents().getNodes().get(l1.getContents().getNodes().size()-1).getParentNode().equals(l2.getContents().getNodes().get(0).getParentNode()))
				return false;
			if(l1.getRange().isBefore(l2.getRange().begin)) {
				return checkType3Threshold(l1, l2);
			} else return checkType3Threshold(l2, l1);
		});
	}

	private boolean checkType3Threshold(Location l1, Location l2) {
		int combinedSize = l1.getAmountOfNodes() + l2.getAmountOfNodes();
		int diff = calculateDiff(l1, l2);
		if(diff == 0)
			return false;
		return calcPercentage(diff, combinedSize) <= Settings.get().getType3GapSize();
	}
}
