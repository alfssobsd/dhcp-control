class AddIsDefaultToGroups < ActiveRecord::Migration
  def change
    add_column :groups, :default, :boolean, :default => true
  end
end
