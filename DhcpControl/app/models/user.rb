class User < ActiveRecord::Base
  attr_accessor :password
  attr_accessible :login, :api_token, :is_ldap, :password, :password_confirmation, :role_ids

  validates_presence_of     :password,  :if => :new_record?                                     
  validates_confirmation_of :password,  :if => :password_changed?
  validates_length_of       :password,  :within => 6..254, :if => :password_changed?
  
  validates :login,   :uniqueness => true,
                      :presence => true  

  before_save :encrypt_password
  before_create :delete_space
  before_update :delete_space

  def delete_space
    self.login.gsub!(/[ \t]/,'');
  end

  def has_password?(submitted_password)
   encrypted_password == encrypt(submitted_password)
  end

  def self.authenticate(login, submitted_password)
   login = find_by_login(login)
   return nil  if login.nil?
   if login.is_ldap
     ldap = Ldap.new
     return login if ldap.is_auth(login.login,submitted_password)
   end
   return login if login.has_password?(submitted_password) and !login.is_ldap
  end

  def self.authenticate_with_salt(id, cookie_salt)
   login = find_by_id(id)
   return nil  if login.nil?
   return login if login.salt == cookie_salt
  end
  
  def self.authenticate_with_api_token(token)
    login = find_by_api_token(token)
    return nil  if login.nil?
    return login
  end

  
  def password_changed?
    return true if !password.nil? and !password.empty?
    return false
  end
  
  def create_record?
    return true if new_record?
    return false
  end
  
  private

  def encrypt_password
    if !password.nil? and !password.empty?
      self.salt      = make_salt if new_record?
      self.api_token = make_token("#{salt}") if new_record?
      self.encrypted_password = encrypt(password)
    end
  end

  def encrypt(string)
    secure_hash("#{salt}--#{string}")
  end

  def make_token(string)
    secure_hash("#{Time.now.utc}--#{string}")
  end
  
  def make_salt
    secure_hash("#{Time.now.utc}--#{password}")
  end

  def secure_hash(string)
    Digest::MD5.hexdigest(string)
  end
end
