package com.moekr.kubernetes.demo.model;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.VolumeMount;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.moekr.kubernetes.demo.util.Constants.NAME_PATTERN;

@Data
@NoArgsConstructor
public class Volume {
	@NotNull
	@Pattern(regexp = NAME_PATTERN)
	private String name;
	@NotNull
	@Pattern(regexp = NAME_PATTERN)
	private String storage;
	@NotBlank
	private String mountPath;

	public Volume(io.fabric8.kubernetes.api.model.Volume volume, VolumeMount mount) {
		this.name = volume.getName();
		this.storage = volume.getPersistentVolumeClaim().getClaimName();
		this.mountPath = mount.getMountPath();
	}

	public io.fabric8.kubernetes.api.model.Volume toVolume() {
		io.fabric8.kubernetes.api.model.Volume volume = new io.fabric8.kubernetes.api.model.Volume();
		volume.setName(name);
		{
			PersistentVolumeClaimVolumeSource source = new PersistentVolumeClaimVolumeSource();
			source.setClaimName(storage);
			volume.setPersistentVolumeClaim(source);
		}
		return volume;
	}

	public VolumeMount toVolumeMount() {
		VolumeMount mount = new VolumeMount();
		mount.setName(name);
		mount.setMountPath(mountPath);
		return mount;
	}
}
