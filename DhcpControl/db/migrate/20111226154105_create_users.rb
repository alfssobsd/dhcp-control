class CreateUsers < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.string :login
      t.string :encrypted_password
      t.string :salt
      t.string :api_token
      t.boolean :is_ldap

      t.timestamps
    end
  end
end
