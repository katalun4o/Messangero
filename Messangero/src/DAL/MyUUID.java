package DAL;

import java.util.UUID;

import com.ianywhere.ultralitejni12.ULjException;

public class MyUUID implements com.ianywhere.ultralitejni12.UUIDValue
{
	UUID id = UUID.randomUUID();
	
	public MyUUID()
	{
		
	}
	
	public MyUUID(String value)
	{
		id = UUID.fromString(value);
	}
	
	public String getString() throws ULjException {
		// TODO Auto-generated method stub
		return id.toString();
	}

	public boolean isNull() {
		// TODO Auto-generated method stub
		return id == null;
	}

	public void set(String value) throws ULjException {
		// TODO Auto-generated method stub
		this.id = UUID.fromString(value);
	}

	public void setNull() throws ULjException {
		// TODO Auto-generated method stub
		id = null;
	}

}
