package earth.terrarium.heracles.client.components;

import com.mojang.math.Divisor;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ClearableGridLayout extends AbstractLayout {

	private final List<LayoutElement> children = new ArrayList<>();
	private final List<CellInhabitant> cellInhabitants = new ArrayList<>();
	private final LayoutSettings defaultCellSettings = LayoutSettings.defaults();
	private int rowSpacing = 0;
	private int columnSpacing = 0;

	public ClearableGridLayout() {
		super(0, 0, 0, 0);
	}

	@Override
	public void arrangeElements() {
		super.arrangeElements();
		int i = 0;
		int j = 0;

		for(ClearableGridLayout.CellInhabitant inhabitant : this.cellInhabitants) {
			i = Math.max(inhabitant.getLastOccupiedRow(), i);
			j = Math.max(inhabitant.getLastOccupiedColumn(), j);
		}

		int[] is = new int[j + 1];
		int[] js = new int[i + 1];

		for(ClearableGridLayout.CellInhabitant inhabitant : this.cellInhabitants) {
			int k = inhabitant.getHeight();
			Divisor divisor = new Divisor(k, 1);

			for(int l = inhabitant.row; l <= inhabitant.getLastOccupiedRow(); ++l) {
				js[l] = Math.max(js[l], divisor.nextInt());
			}

			int l = inhabitant.getWidth();
			Divisor divisor2 = new Divisor(l, 1);

			for(int m = inhabitant.column; m <= inhabitant.getLastOccupiedColumn(); ++m) {
				is[m] = Math.max(is[m], divisor2.nextInt());
			}
		}

		int[] ks = new int[j + 1];
		int[] ls = new int[i + 1];
		ks[0] = 0;

		for(int k = 1; k <= j; ++k) {
			ks[k] = ks[k - 1] + is[k - 1] + this.columnSpacing;
		}

		ls[0] = 0;

		for(int k = 1; k <= i; ++k) {
			ls[k] = ls[k - 1] + js[k - 1] + this.rowSpacing;
		}

		for(ClearableGridLayout.CellInhabitant cellInhabitant3 : this.cellInhabitants) {
			int l = 0;

			for(int n = cellInhabitant3.column; n <= cellInhabitant3.getLastOccupiedColumn(); ++n) {
				l += is[n];
			}

			cellInhabitant3.setX(this.getX() + ks[cellInhabitant3.column], l);
			int n = 0;

			for(int m = cellInhabitant3.row; m <= cellInhabitant3.getLastOccupiedRow(); ++m) {
				n += js[m];
			}

			cellInhabitant3.setY(this.getY() + ls[cellInhabitant3.row], n);
		}

		this.width = ks[j] + is[j];
		this.height = ls[i] + js[i];
	}

	public void clear() {
		this.children.clear();
		this.cellInhabitants.clear();
	}

	public <T extends LayoutElement> T addChild(T child, int row, int column) {
		return this.addChild(child, row, column, this.newCellSettings());
	}

	public <T extends LayoutElement> T addChild(T child, int row, int column, LayoutSettings settings) {
		this.cellInhabitants.add(new ClearableGridLayout.CellInhabitant(child, row, column, settings));
		this.children.add(child);
		return child;
	}

	public <T extends LayoutElement> T addChild(T child, int row, int column, Consumer<LayoutSettings> settingsConsumer) {
		LayoutSettings settings = this.newCellSettings();
		settingsConsumer.accept(settings);
		this.cellInhabitants.add(new ClearableGridLayout.CellInhabitant(child, row, column, settings));
		this.children.add(child);
		return child;
	}

	public ClearableGridLayout columnSpacing(int columnSpacing) {
		this.columnSpacing = columnSpacing;
		return this;
	}

	public ClearableGridLayout rowSpacing(int rowSpacing) {
		this.rowSpacing = rowSpacing;
		return this;
	}

	public ClearableGridLayout spacing(int spacing) {
		return this.columnSpacing(spacing).rowSpacing(spacing);
	}

	@Override
	public void visitChildren(Consumer<LayoutElement> consumer) {
		this.children.forEach(consumer);
	}

	public LayoutSettings newCellSettings() {
		return this.defaultCellSettings.copy();
	}

	public RowHelper rows(int startingRow, int columns) {
		return new RowHelper(this, startingRow, columns);
	}

	static class CellInhabitant extends AbstractLayout.AbstractChildWrapper {
		final int row;
		final int column;

		CellInhabitant(LayoutElement layoutElement, int i, int j, LayoutSettings layoutSettings) {
			super(layoutElement, layoutSettings.getExposed());
			this.row = i;
			this.column = j;
		}

		public int getLastOccupiedRow() {
			return this.row;
		}

		public int getLastOccupiedColumn() {
			return this.column;
		}
	}

	public final class RowHelper {
		private final ClearableGridLayout layout;
		private final int columns;
		private final int rowOffset;
		private int index;

		RowHelper(ClearableGridLayout layout, int rowOffset, int columns) {
			this.layout = layout;
			this.rowOffset = rowOffset;
			this.columns = columns;
		}

		public <T extends LayoutElement> T addChild(T child) {
			return this.addChild(child, this.layout.newCellSettings());
		}

		public <T extends LayoutElement> T addChild(T child, Consumer<LayoutSettings> settingsConsumer) {
			LayoutSettings settings = this.layout.newCellSettings();
			settingsConsumer.accept(settings);
			return this.addChild(child, settings);
		}

		public <T extends LayoutElement> T addChild(T child, LayoutSettings layoutSettings) {
			int i = this.index / this.columns;
			int j = this.index % this.columns;
			if (j + 1 > this.columns) {
				++i;
				j = 0;
				this.index = Mth.roundToward(this.index, this.columns);
			}

			this.index += 1;
			return this.layout.addChild(child, this.rowOffset + i, j, layoutSettings);
		}
	}
}
