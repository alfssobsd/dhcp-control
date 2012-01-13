class CreateGroups < ActiveRecord::Migration
  def change
    create_table :groups do |t|
      t.integer :subnet_id
      t.string :name
      t.text :options

      t.timestamps
    end
  end
end
