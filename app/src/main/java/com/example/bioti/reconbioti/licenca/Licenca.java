package com.example.bioti.reconbioti.licenca;

import com.neurotec.licensing.NLicense;
import com.example.bioti.reconbioti.util.FileUtils;
import com.example.bioti.reconbioti.util.IOUtils;


import java.io.File;
import java.io.IOException;

public final class Licenca {

	// ===========================================================
	// Private fields
	// ===========================================================

	private String mFolderPath;
	private String mName;
	private String mLicenseFilePath;
	private String mDeactivatedLicenseFilePath;
	private String mSerialNumberPath;
	private String mDeviceIDPath;
	private String mDeactivationIDPath;

	// ===========================================================
	// Package private constructor
	// ===========================================================

	Licenca(String folderPath, String name) {
		if (folderPath == null) throw new NullPointerException("folderPath");
		if (name == null) throw new NullPointerException("name");

        //Nome da pasta que se encontra as licenças
		this.mFolderPath = folderPath;
        //Nome da licença
		this.mName = name;
        //Path licenças
		this.mLicenseFilePath = IOUtils.combinePath(mFolderPath, mName + GerenciadorServicoLicenca.EXTENSION_LICENSE_FILE);
		//path licenças desativadas, lembrando que se não tiver licenças desativadas esse valor será nulo
        this.mDeactivatedLicenseFilePath = IOUtils.combinePath(mFolderPath, mName + GerenciadorServicoLicenca.EXTENSION_LICENSE_FILE_DEACTIVATED);
		//Path números de serie
        this.mSerialNumberPath = IOUtils.combinePath(mFolderPath, mName + GerenciadorServicoLicenca.EXTENSION_SERIAL_NUMBER_FILE);
	    //Patch ID
		this.mDeviceIDPath = IOUtils.combinePath(mFolderPath, mName + GerenciadorServicoLicenca.EXTENSION_DEVICE_ID_FILE);
		this.mDeactivationIDPath = IOUtils.combinePath(mFolderPath, mName + "_deactivation" + GerenciadorServicoLicenca.EXTENSION_DEVICE_ID_FILE);


	}

	// ===========================================================
	// Private methods
	// ===========================================================

	//Renomear Licença
    private void rename() {
		new File(mLicenseFilePath).renameTo(new File(mDeactivatedLicenseFilePath));
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	//Verifica se existe licença no path de licenças
    public boolean isActivated() {
		return new File(mLicenseFilePath).exists();
	}

    //Verifica se existe um número de série no path de numeros de serie
    public boolean hasSerialNumber() {
		return new File(mSerialNumberPath).exists();
	}


	//Realiza a ativação de uma licença, verifica primeiro se é um número de série, se for gera o id
	// grava o id no diretorio de licencças e se estiver online realiza a
	// ativação da licença por meio do id e grava as licenças geradas no diretorio
    public void activate(boolean online) throws IOException {
		if (hasSerialNumber()) {
			//String serialNumber = FileUtils.readPrintableCharacters(mSerialNumberPath);
            //String deviceID = NLicense.generateID(serialNumber);
            String deviceID = FileUtils.readPrintableCharacters(mDeviceIDPath);

                if (deviceID != null) {
                    FileUtils.write(mDeviceIDPath, deviceID);

                    if (online == true) {
                        String license = NLicense.activateOnline(deviceID);
                        if (license != null) {
                            FileUtils.write(mLicenseFilePath, license);
                        }
                    }
                }


		}

	}


    //desativa licença
	public void deactivate(boolean online) throws IOException {
		if (isActivated()) {
			String license = FileUtils.readPrintableCharacters(mLicenseFilePath);
			if (online) {
				NLicense.deactivateOnline(license);
				if (hasSerialNumber()) {
					rename();
				}
			} else {
				String deactivationID = NLicense.generateDeactivationIDForLicense(license);
				if (deactivationID != null) {
					FileUtils.write(mDeactivationIDPath, deactivationID);
				}
			}
		}
	}

	//Retorna o nome da licença
    public String getName() {
		return mName;
	}

	//retorna o path das licenças
    public String getLicensePath() {
		return mLicenseFilePath;
	}

	@Override
	public String toString() {
		return mName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mName == null) ? 0 : mName.hashCode());
		result = prime * result + ((mFolderPath == null) ? 0 : mFolderPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Licenca other = (Licenca) obj;
		if (mName == null) {
			if (other.mName != null) return false;
		} else if (!mName.equals(other.mName)) return false;
		if (mFolderPath == null) {
			if (other.mFolderPath != null) return false;
		} else if (!mFolderPath.equals(other.mFolderPath)) return false;
		return true;
	}

}
